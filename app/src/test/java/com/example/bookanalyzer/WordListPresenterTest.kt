package com.example.bookanalyzer

import com.example.bookanalyzer.domain.models.WordListRowEntity
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.mvp.presenters.WordListPresenter
import com.example.bookanalyzer.mvp.views.WordListView
import com.example.bookanalyzer.mvp.views.`WordListView$$State`
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class WordListPresenterTest {

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var presenter: WordListPresenter

    @RelaxedMockK
    private lateinit var viewState: `WordListView$$State`

    @RelaxedMockK
    private lateinit var view: WordListView

    @RelaxedMockK
    private lateinit var repository: WordListRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        presenter = WordListPresenter(repository)
        presenter.attachView(view)
        presenter.setViewState(viewState)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `onProgressChanged with zero progress`() {
        val pos = 0

        presenter.onProgressChanged(pos)

        verify { viewState.setPositionViewText(ofType(String::class)) }
        verify { viewState.scrollToPosition(pos + 1) }
    }

    @Test
    fun `onProgressChanged with non-zero progress`() {
        val leftBorder = 0
        val rightBorder = 10000
        val pos = (leftBorder..rightBorder).random()

        presenter.onProgressChanged(pos)
        verify { viewState.setPositionViewText(ofType(String::class)) }
        verify { viewState.scrollToPosition(pos) }
    }

    @Test
    fun `onOptionsItemBackSelected should finish activity`() {
        presenter.onOptionsItemBackSelected()
        verify { viewState.finishActivity() }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `onViewCreated should create word list and init bottom panel`() =
        testDispatcher.runBlockingTest {
            coEvery { repository.getWordList(any()) } returns getSomeRowEntityList()

            presenter.onViewCreated(0)

            coVerify { repository.getWordList(any()) }
            coVerify { viewState.setupWordItems(any()) }
            coVerify { viewState.setPositionViewText(ofType(String::class)) }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `onViewCreated should do nothing`() = testDispatcher.runBlockingTest {
        val analysisId = 0
        coEvery { repository.getWordList(analysisId) } returns null

        presenter.onViewCreated(analysisId)

        coVerify { repository.getWordList(analysisId) }
        coVerify (exactly = 0){ viewState.setupWordItems(any()) }
        coVerify (exactly = 0){ viewState.setPositionViewText(ofType(String::class)) }
    }

    private fun getSomeRowEntityList(): ArrayList<WordListRowEntity> {
        val list = ArrayList<WordListRowEntity>()
        for (i in 0..10) {
            list.add(WordListRowEntity("123", 123, 1))
        }
        return (list)
    }

}

