package com.example.bookanalyzer

import com.example.bookanalyzer.domain.models.BookInfoEntity
import com.example.bookanalyzer.domain.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.presenters.BookInfoPresenter
import com.example.bookanalyzer.mvp.views.BookInfoView
import com.example.bookanalyzer.mvp.views.`BookInfoView$$State`
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookInfoPresenterTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var presenter: BookInfoPresenter

    @RelaxedMockK
    private lateinit var viewState: `BookInfoView$$State`

    @RelaxedMockK
    private lateinit var view: BookInfoView

    @RelaxedMockK
    private lateinit var repository: BookInfoRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        presenter = BookInfoPresenter(repository)
        presenter.attachView(view)
        presenter.setViewState(viewState)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `onViewCreated should set Views text`() = testCoroutineRule.runBlockingTest {
        coEvery { repository.readInfo(any()) } returns BookInfoEntity()

        presenter.onViewCreated(0)

        coVerify { repository.readInfo(any()) }
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