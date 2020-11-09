package com.example.bookanalyzer

import com.example.bookanalyzer.domain.models.ShowedAnalysisEntity
import com.example.bookanalyzer.domain.repositories.BookAnalysisRepository
import com.example.bookanalyzer.mvp.presenters.BookAnalysisPresenter
import com.example.bookanalyzer.mvp.views.BookAnalysisView
import com.example.bookanalyzer.mvp.views.`BookInfoView$$State`
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookAnalysisPresenterTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var presenter: BookAnalysisPresenter

    @RelaxedMockK
    private lateinit var viewState: `BookInfoView$$State`

    @RelaxedMockK
    private lateinit var view: BookAnalysisView

    @RelaxedMockK
    private lateinit var repository: BookAnalysisRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        presenter = BookAnalysisPresenter(repository)
        presenter.attachView(view)
        presenter.setViewState(viewState)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `onViewCreated should set Views text`() = testCoroutineRule.runBlockingTest {
        coEvery { repository.getAnalysis(any()) } returns ShowedAnalysisEntity()

        presenter.onViewCreated(0)

        coVerify { repository.getAnalysis(any()) }
        coVerify { viewState.setViewsText(any()) }
    }

    @Test
    fun `onOptionsItemBackSelected should finish activity`() {
        presenter.onOptionsItemBackSelected()

        verify { viewState.finishActivity() }
    }

    @Test
    fun `onWordListButtonClicked should start wordListActivity`() {
        presenter.onWordListButtonClicked(0)

        verify { viewState.startWordListActivity(any()) }
    }
}