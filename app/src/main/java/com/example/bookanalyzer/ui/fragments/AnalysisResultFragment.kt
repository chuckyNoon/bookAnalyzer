package com.example.bookanalyzer.ui.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.MyNavigation
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.databinding.FragmentAnalysisResultBinding
import com.example.bookanalyzer.domain.repositories.BookAnalysisRepository
import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AnalysisParamsAdapter
import com.example.bookanalyzer.view_models.AnalysisResultViewModel
import com.example.bookanalyzer.view_models.AnalysisResultViewModelFactory
import com.squareup.picasso.Picasso
import javax.inject.Inject


const val EXTRA_RESULT_FRAGMENT = "123123"

class AnalysisResultFragment : Fragment() {

    companion object {
        fun newInstance(extra: ResultFragmentExtra) =
            AnalysisResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_RESULT_FRAGMENT, extra)
                }
            }
    }

    var exitAnimationFinished = false
    var enterAnimationFinished = false

    private val extra: ResultFragmentExtra? by lazy {
        arguments?.getSerializable(EXTRA_RESULT_FRAGMENT) as ResultFragmentExtra?
    }

    private var interaction: ResultFragmentInteraction? = null

    private var binding: FragmentAnalysisResultBinding? = null

    @Inject
    lateinit var resourceManager: ResourceManager

    @Inject
    lateinit var repository: BookAnalysisRepository

    private lateinit var viewModel: AnalysisResultViewModel

    fun startExitAnimation() {
        binding?.analysisParamsRecycler?.visibility = View.INVISIBLE
        binding?.textView?.visibility = View.INVISIBLE
        val startY = 0f
        val endY = extra?.yOffset ?: 0f
        ObjectAnimator.ofFloat(binding?.body, "translationY", startY, endY).apply {
            duration = if (endY != startY) 200 else 0
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    exitAnimationFinished = true
                    activity?.onBackPressed()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            start()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = context as? ResultFragmentInteraction
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        MyApp.appComponent.inject(this)
        val viewModelFactory = AnalysisResultViewModelFactory(repository, resourceManager)
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            AnalysisResultViewModel::class.java
        )
        postponeEnterTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisResultBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBookPreview()
        setupToolBar()
        setupRecyclerView()
        setupObservers()
        if (savedInstanceState != null){
            enterAnimationFinished = true
        }
        extra?.analysisId?.let {
            viewModel.onViewCreated(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            viewModel.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBookPreview() {
        val cell = extra?.cell ?: return
        binding?.bookPreview?.apply {
            bookNameView.text = cell.title
            wordCountView.text = cell.uniqueWordCount
            bookFormatView.text = cell.format
            bookAuthorView.text = cell.author
            progressBar.apply {
                max = 20000
                progress = cell.barProgress
            }
            val defaultBookImage =
                ResourcesCompat.getDrawable(resources, R.drawable.book, null) ?: return
            val appFilesDir = requireActivity().filesDir ?: return

            cell.imgPath?.let {
                Picasso.get()
                    .load(appFilesDir.resolve(cell.imgPath))
                    .into(bookImage)
            } ?: run {
                bookImage.setImageDrawable(defaultBookImage)
            }
        }
    }

    private fun setupToolBar() {
        binding?.toolbar?.title = resources.getString(R.string.analysis_activity_title)
        binding?.toolbar?.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding?.toolbar)
    }

    private fun setupRecyclerView() {
        binding?.analysisParamsRecycler?.apply {
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupObservers() {
        viewModel.uniqueWordCount.observe(viewLifecycleOwner, Observer { uniqueWordCount->
            if (uniqueWordCount == null){
                return@Observer
            }
            binding?.bookPreview?.wordCountView?.text = "$uniqueWordCount words"
            binding?.bookPreview?.progressBar?.progress = uniqueWordCount
        })

        viewModel.cells.observe(viewLifecycleOwner, Observer { cells ->
            if (cells == null) {
                return@Observer
            }
            val adapter = binding?.analysisParamsRecycler?.adapter as? AnalysisParamsAdapter
            if (adapter == null) {
                val newAdapter = AnalysisParamsAdapter(wordListButtonInteraction)
                newAdapter.setupCells(cells)
                binding?.analysisParamsRecycler?.adapter = newAdapter
            } else {
                adapter.setupCells(cells)
            }
            startPostponedEnterTransition()
            if (!enterAnimationFinished){
                startEnterAnimation()
            }
        })

        viewModel.navigation.observe(viewLifecycleOwner, Observer { navigation ->
            if (navigation == null) {
                return@Observer
            }
            when (navigation) {
                is MyNavigation.Exit -> activity?.onBackPressed()
                is MyNavigation.ToWordsFragment -> interaction?.onWordListButtonClicked(navigation.analysisId)
            }
        })
    }

    private fun startEnterAnimation(){
        binding?.analysisParamsRecycler?.visibility = View.INVISIBLE
        binding?.textView?.visibility = View.INVISIBLE
        val startY = extra?.yOffset ?: 0f
        val endY = 0f
        ObjectAnimator.ofFloat(binding?.body, "translationY", startY, endY).apply {
            duration = if (startY != endY) 200 else 0
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    binding?.analysisParamsRecycler?.visibility = View.VISIBLE
                    binding?.textView?.visibility = View.VISIBLE
                    enterAnimationFinished = true
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            start()
        }
    }

    private val wordListButtonInteraction =
        object : AnalysisParamsAdapter.WordListButtonInteraction {
            override fun onButtonClicked() {
                extra?.analysisId?.let {
                    viewModel.onWordListButtonClicked(it)
                }
            }
        }

    interface ResultFragmentInteraction {
        fun onWordListButtonClicked(analysisId: Int)
    }
}