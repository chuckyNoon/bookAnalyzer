package com.example.bookanalyzer

import com.example.bookanalyzer.domain.models.BookPreviewEntity
import com.example.bookanalyzer.domain.repositories.BookInfoRepository
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.mvp.presenters.BookInfoPresenter
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.example.bookanalyzer.mvp.views.BookInfoView
import com.example.bookanalyzer.mvp.views.StartScreenView
import com.example.bookanalyzer.mvp.views.`BookInfoView$$State`
import com.example.bookanalyzer.mvp.views.`StartScreenView$$State`
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class StartScreenPresenterTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var presenter: StartScreenPresenter

    @RelaxedMockK
    private lateinit var viewState: `StartScreenView$$State`

    @RelaxedMockK
    private lateinit var view: StartScreenView

    @RelaxedMockK
    private lateinit var repository: StartScreenRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        presenter = StartScreenPresenter(repository)
        presenter.attachView(view)
        presenter.setViewState(viewState)
    }

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    @Test
    fun `onViewCreated should indicate loading, build list, then end loading`() =
        testCoroutineRule.runBlockingTest {
                coEvery { repository.getCompleteDataList() } returns arrayListOf(BookPreviewEntity())
                presenter.onViewCreated()
                coVerify { viewState.showLoadingStateView() }
                coVerify { viewState.moveLoadingStateViewUp(any()) }
                coVerify { viewState.setLoadingStateViewText(any()) }
                coVerify { viewState.showBookList(any()) }
                coVerify { viewState.updateLoadingStateView(any(), any(), any()) }
                coVerify { viewState.moveLoadingStateViewDown(any()) }
                coVerify { viewState.hideLoadingStateView() }
        }
}


class TestCoroutineRule() : TestRule {

    @ExperimentalCoroutinesApi
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @ExperimentalCoroutinesApi
    override fun apply(base: Statement?, description: Description?) = object : Statement() {
        override fun evaluate() {
            Dispatchers.setMain(testCoroutineDispatcher)

            base?.evaluate()

            Dispatchers.resetMain()
            testCoroutineScope.cleanupTestCoroutines()
        }
    }

    @ExperimentalCoroutinesApi
    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
        testCoroutineScope.runBlockingTest(block)
}
