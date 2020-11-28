package com.example.bookanalyzer

import com.example.bookanalyzer.domain.models.WordEntity
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.mvp.views.WordListView
import com.example.bookanalyzer.mvp.views.`WordListView$$State`
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WordListPresenterTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

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
        MockKAnnotations.init(this)
        presenter = WordListPresenter(repository)
        presenter.attachView(view)
        presenter.setViewState(viewState)
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
        testCoroutineRule.runBlockingTest {
            coEvery { repository.getWordEntities(any()) } returns getSomeRowEntityList()

            presenter.onViewCreated(0)

            coVerify { repository.getWordEntities(any()) }
            coVerify { viewState.setupCells(any()) }
            coVerify { viewState.setPositionViewText(ofType(String::class)) }
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `onViewCreated should do nothing`() = testCoroutineRule.runBlockingTest {
        val analysisId = 0
        coEvery { repository.getWordEntities(analysisId) } returns null

        presenter.onViewCreated(analysisId)

        coVerify { repository.getWordEntities(analysisId) }
        coVerify(exactly = 0) { viewState.setupCells(any()) }
        coVerify(exactly = 0) { viewState.setPositionViewText(ofType(String::class)) }
    }

    private fun getSomeRowEntityList(): ArrayList<WordEntity> {
        val list = ArrayList<WordEntity>()
        for (i in 0..10) {
            list.add(WordEntity("123", 123, 1))
        }
        return (list)
    }
}

