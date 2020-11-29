package com.example.bookanalyzer.ui.fragments

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.common.FileUtils
import com.example.bookanalyzer.databinding.FragmentBooksBinding
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.ui.adapters.book_items_adapter.BookCell
import com.example.bookanalyzer.ui.adapters.book_items_adapter.BooksAdapter
import com.example.bookanalyzer.ui.adapters.book_items_adapter.SimpleItemTouchHelperCallback
import com.example.bookanalyzer.ui.adapters.side_menu_adapter.SideMenuRowCell
import com.example.bookanalyzer.ui.adapters.side_menu_adapter.SideMenuRowsAdapter
import com.example.bookanalyzer.ui.fragments.dialogs.FirstLaunchDialog
import com.example.bookanalyzer.ui.fragments.dialogs.SearchSettingsDialog
import com.example.bookanalyzer.view_models.BooksViewModel
import com.example.bookanalyzer.view_models.BooksViewModelFactory
import java.io.File
import java.io.Serializable
import javax.inject.Inject

class BooksFragment() : Fragment(), SearchSettingsDialog.OnSearchSettingsSelected,
    FirstLaunchDialog.OnSelectedLaunchOption {

    companion object {
        private const val TAG_FIRST_LAUNCH_DIALOG = "123"
        private const val TAG_PREFERENCES = "APP_PREFERENCES"
        private const val TAG_FIRST_LAUNCH = "firstLaunch"
        private const val TAG_SEARCH_SETTING_DIALOG = "124"
        private const val REQUEST_PERMISSION_FOR_BOOK_ADD_CODE = 1
        private const val REQUEST_PERMISSION_FOR_BOOK_SEARCH_CODE = 2
        private const val SELECT_FILE_REQUEST_CODE = 123
    }

    private var interaction: BooksFragmentInteraction? = null

    private var binding: FragmentBooksBinding? = null

    @Inject
    lateinit var resourceManager: ResourceManager

    @Inject
    lateinit var repository: StartScreenRepository

    private lateinit var viewModel: BooksViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = context as? BooksFragmentInteraction
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        MyApp.appComponent.inject(this)
        val viewModelFactory = BooksViewModelFactory(repository, resourceManager)
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            BooksViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBooksBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        setupSideMenu()
        setupRecyclerView()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStart() {
        super.onStart()
        if (isFirstApplicationLaunch()) {
            FirstLaunchDialog().show(childFragmentManager, TAG_FIRST_LAUNCH_DIALOG)
        } else {
            viewModel.onStart()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            viewModel.onOptionsMenuItemSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val bookPath = FileUtils().getPathByUri(requireContext(), uri)
                bookPath?.let {
                    viewModel.onActivityResult(bookPath)
                }
            }
        }
    }

    override fun onSelectedLaunchOption(isScanSelected: Boolean) {
        if (isScanSelected) {
            requestReadPermission(REQUEST_PERMISSION_FOR_BOOK_SEARCH_CODE)
        } else {
            Toast.makeText(
                context,
                resources.getString(R.string.search_declined_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding?.drawerLayout?.close()
            when (requestCode) {
                REQUEST_PERMISSION_FOR_BOOK_ADD_CODE -> {
                    val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*")
                    startActivityForResult(intent, SELECT_FILE_REQUEST_CODE)
                }
                REQUEST_PERMISSION_FOR_BOOK_SEARCH_CODE -> {
                    showSearchSettingsDialog()
                }
            }
        }
    }

    private fun showSearchSettingsDialog() {
        SearchSettingsDialog().show(childFragmentManager, TAG_SEARCH_SETTING_DIALOG)
    }

    override fun onSearchSettingsSelected(bookFormats: ArrayList<String>, searchRootDir: File) {
        viewModel.onSearchSettingsSelected(bookFormats, searchRootDir)
    }

    private fun showLoadingStateView() {
        binding?.loadingStateView?.visibility = View.VISIBLE
    }

    private fun hideLoadingStateView() {
        binding?.loadingStateView?.visibility = View.INVISIBLE
    }

    private fun moveLoadingStateViewUp(animDuration: Int) {
        ObjectAnimator.ofFloat(binding?.loadingStateView, "translationY", 0f).apply {
            duration = animDuration.toLong()
            start()
        }
    }

    private fun moveLoadingStateViewDown(animDuration: Int) {
        val height = binding?.loadingStateView?.height?.toFloat() ?: return
        ObjectAnimator.ofFloat(binding?.loadingStateView, "translationY", height).apply {
            duration = animDuration.toLong()
            start()
        }
    }

    private fun setLoadingStateViewText(stringResId: Int) {
        binding?.loadingStateView?.text = resources.getString(stringResId)
    }

    private fun updateLoadingStateView(
        stringResId: Int,
        animDownDuration: Long,
        animUpDuration: Long
    ) {
        val height = binding?.loadingStateView?.height?.toFloat() ?: return

        ObjectAnimator.ofFloat(binding?.loadingStateView, "translationY", height).apply {
            duration = animDownDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    setLoadingStateViewText(stringResId)
                    moveLoadingStateViewUp(animUpDuration.toInt())
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            start()
        }
    }

    private fun setupObservers() {
        viewModel.bookCells.observe(viewLifecycleOwner, Observer { bookCells ->
            if (bookCells == null) {
                return@Observer
            }
            val adapter = binding?.booksRecycler?.adapter as BooksAdapter?
            if (adapter == null) {
                val defaultBookImage = ResourcesCompat.getDrawable(resources, R.drawable.book, null)
                val newAdapter = BooksAdapter(
                    requireActivity().filesDir,
                    defaultBookImage,
                    bookInteraction
                )
                newAdapter.setupBooks(bookCells)
                binding?.booksRecycler?.adapter = newAdapter
            } else {
                adapter.setupBooks(bookCells)
            }
        })

        viewModel.contentLoadingState.observe(viewLifecycleOwner, Observer { loadingState ->
            if (loadingState == null) {
                return@Observer
            }
            when (loadingState) {
                BooksViewModel.ContentLoadingState.Active -> {
                    showLoadingStateView()
                    moveLoadingStateViewUp(300)
                    setLoadingStateViewText(R.string.loading_content_started)
                }
                BooksViewModel.ContentLoadingState.Finished -> {
                    updateLoadingStateView(R.string.loading_content_ended, 250, 300)
                }
                else -> {
                    moveLoadingStateViewDown(250)
                    hideLoadingStateView()
                }
            }
        })

        viewModel.sideMenuState.observe(viewLifecycleOwner, Observer { sideMenuState ->
            if (sideMenuState == null) {
                return@Observer
            }
            when (sideMenuState) {
                BooksViewModel.SideMenuState.Showed -> {
                    binding?.drawerLayout?.open()
                }
            }
        })

        viewModel.bookToAnalyze.observe(viewLifecycleOwner, Observer { bookPath ->
            if (bookPath == null) {
                return@Observer
            }
            interaction?.onNotAnalyzedBookClicked(bookPath)
        })

        viewModel.showBookIntent.observe(viewLifecycleOwner, Observer { intention ->
            if (intention == null) {
                return@Observer
            }
            interaction?.onAnalyzedBookClicked(intention)
        })
    }

    private fun isFirstApplicationLaunch(): Boolean {
        val prefs = requireActivity().getSharedPreferences(TAG_PREFERENCES, Context.MODE_PRIVATE)
        return if (!prefs.contains(TAG_FIRST_LAUNCH)) {
            prefs.edit().putBoolean(TAG_FIRST_LAUNCH, true).apply()
            (true)
        } else {
            (false)
        }
    }

    private fun setupToolBar() {
        binding?.toolbar?.title = resources.getString(R.string.start_screen_title)
        binding?.toolbar?.setNavigationIcon(R.drawable.baseline_menu_24)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding?.toolbar)
    }

    private fun setupRecyclerView() {
        binding?.booksRecycler?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
        }
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(viewModel)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding?.booksRecycler)
    }

    private fun requestReadPermission(requestCode: Int) {
        val requiredPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissions(requiredPermissions, requestCode)
    }

    private fun setupSideMenu() {
        val sideMenuRowCells = ArrayList<SideMenuRowCell>()
        sideMenuRowCells.add(
            SideMenuRowCell(
                resources.getString(R.string.select_new_file),
                R.drawable.baseline_folder_24
            )
        )
        sideMenuRowCells.add(
            SideMenuRowCell(
                resources.getString(R.string.search_books),
                R.drawable.baseline_search_24
            )
        )
        val sideMenuAdapter = SideMenuRowsAdapter(sideMenuRowInteraction)
        sideMenuAdapter.setupCells(sideMenuRowCells)
        binding?.sideMenu?.sideMenuList?.apply{
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            layoutManager = LinearLayoutManager(context)
            adapter = sideMenuAdapter
        }
    }

    private val bookInteraction = object : BooksAdapter.BookInteraction {
        override fun onBookClicked(view: View, position: Int) {
            fun getY(view:View?) : Float{
                val pos = IntArray(2)
                view?.getLocationOnScreen(pos)
                return pos[1].toFloat()
            }
            if (view.background is TransitionDrawable) {
                val itemBackgroundTransition = view.background as TransitionDrawable
                itemBackgroundTransition.startTransition(200)
                itemBackgroundTransition.reverseTransition(200)
            }
            var yItemOffset = getY(view) - getY(binding?.booksRecycler)
            if (yItemOffset < 0){
                binding?.booksRecycler?.scrollToPosition(position)
                yItemOffset = 0f
            }
            viewModel.onBookClicked(position, yItemOffset)
        }
    }

    private val sideMenuRowInteraction = object : SideMenuRowsAdapter.SideMenuRowInteraction {
        override fun onRowClicked(position: Int) {
            when (position) {
                //to remake with enums
                0 -> requestReadPermission(REQUEST_PERMISSION_FOR_BOOK_ADD_CODE)
                1 -> requestReadPermission(REQUEST_PERMISSION_FOR_BOOK_SEARCH_CODE)
            }
        }
    }

    interface BooksFragmentInteraction {
        fun onNotAnalyzedBookClicked(bookPath: String)
        fun onAnalyzedBookClicked(intention: ShowBookIntention)
    }
}

data class ShowBookIntention(
    val cell: BookCell? = null,
    val analysisId: Int,
    val yOffset: Float? = null
) : Serializable