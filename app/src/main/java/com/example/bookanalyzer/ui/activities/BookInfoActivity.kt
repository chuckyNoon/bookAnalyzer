package com.example.bookanalyzer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ActivityBookInfoBinding
import com.example.bookanalyzer.mvp.presenters.BookInfoPresenter
import com.example.bookanalyzer.domain.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class BookInfoActivity : MvpAppCompatActivity(),
    BookInfoView {

    private lateinit var binding: ActivityBookInfoBinding

    @Inject
    lateinit var repository: BookInfoRepository

    @InjectPresenter
    lateinit var presenter: BookInfoPresenter

    @ProvidePresenter
    fun provideStartScreenPresenter(): BookInfoPresenter {
        MyApp.appComponent.inject(this)
        return BookInfoPresenter(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolBar()
        setupWordListButton()
        selectLaunchOption(savedInstanceState != null)
    }

    private fun selectLaunchOption(isActivityRecreated: Boolean) {
        val analysisId = getAnalysisIdFromIntent()
        analysisId?.let {
            if (!isActivityRecreated) {
                presenter.onViewCreated(analysisId)
            }
        }
    }

    private fun getAnalysisIdFromIntent(): Int? {
        return intent.extras?.getInt(EXTRA_ANALYSIS_ID)
    }

    private fun setupToolBar() {
        binding.toolbar.title = resources.getString(R.string.info_activity_title)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(binding.toolbar)
    }

    private fun setupWordListButton() {
        val analysisId = getAnalysisIdFromIntent()
        analysisId?.let {
            binding.toWordListButton.setOnClickListener {
                presenter.onWordListButtonClicked(analysisId)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setViewsText(bookInfoModel: BookInfoModel) {
        binding.bookNameView.text = bookInfoModel.path
        binding.uniqWordView.text = bookInfoModel.uniqueWordCount
        binding.allWordCountView.text = bookInfoModel.allWordCount
        binding.allCharCountView.text = bookInfoModel.allCharsCount
        binding.avgSentenceLenInWordsView.text = bookInfoModel.avgSentenceLenInWrd
        binding.avgSentenceLenInCharsView.text = bookInfoModel.avgSentenceLenInChr
        binding.avgWordLenView.text = bookInfoModel.avgWordLen
    }

    override fun startWordListActivity(analysisId: Int) {
        val intent = Intent(this, WordListActivity::class.java).apply {
            putExtra(EXTRA_ANALYSIS_ID, analysisId)
        }
        startActivity(intent)
    }

    override fun finishActivity() {
        finish()
    }
}

data class BookInfoModel(
    var path: String,
    var uniqueWordCount: String,
    var allWordCount: String,
    var allCharsCount: String,
    var avgSentenceLenInWrd: String,
    var avgSentenceLenInChr: String,
    var avgWordLen: String
)