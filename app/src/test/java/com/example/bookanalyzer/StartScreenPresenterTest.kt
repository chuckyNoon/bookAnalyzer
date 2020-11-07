package com.example.bookanalyzer

import com.example.bookanalyzer.domain.models.BookPreviewEntity
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.example.bookanalyzer.mvp.views.StartScreenView
import com.example.bookanalyzer.mvp.views.`StartScreenView$$State`
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import kotlin.time.ExperimentalTime

class StartScreenPresenterTest {

    //@get:Rule
    //val testCoroutineRule = TestCoroutineRule()

    private lateinit var presenter: StartScreenPresenter

    @RelaxedMockK
    private lateinit var viewState: `StartScreenView$$State`

    @RelaxedMockK
    private lateinit var view: StartScreenView

    @RelaxedMockK
    private lateinit var repository: StartScreenRepository

    private fun getUsedMocks() = arrayOf<Any>(repository, viewState)

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        //Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        presenter = StartScreenPresenter(repository)
        presenter.attachView(view)
        presenter.setViewState(viewState)
    }

    /*@ExperimentalTime
    @ExperimentalCoroutinesApi
    @Test
    fun `onViewCreated should indicate loading, build list, then end loading`() =
        testCoroutineRule.runBlockingTest {
            coEvery { repository.getCompleteDataList() } returns arrayListOf(BookPreviewEntity())

            presenter.onViewCreated()
            advanceUntilIdle()

            coVerify { viewState.showLoadingStateView() }
            coVerify { viewState.moveLoadingStateViewUp(any()) }
            coVerify { viewState.setLoadingStateViewText(any()) }

            coVerify { repository.getCompleteDataList() }
            coVerify { viewState.showBookList(any()) }

            coVerify { viewState.updateLoadingStateView(any(), any(), any()) }
            coVerify { viewState.moveLoadingStateViewDown(any()) }
            coVerify { viewState.hideLoadingStateView() }
            confirmVerified(*getUsedMocks())
        }

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    @Test
    fun `onSelectedSearchSetting should build list from new Data`() =
        testCoroutineRule.runBlockingTest {
            val formats = arrayListOf("epub", "txt")
            val rootDir = File("")
            coEvery { repository.getInitialDataList(any()) } returns arrayListOf(BookPreviewEntity())
            coEvery { repository.getCompleteDataList() } returns arrayListOf(BookPreviewEntity())

            presenter.onSelectedSearchSettings(formats, rootDir)
            advanceUntilIdle()

            //loading start
            coVerify { viewState.showLoadingStateView() }
            coVerify { viewState.moveLoadingStateViewUp(any()) }
            coVerify { viewState.setLoadingStateViewText(any()) }
            //build initial list
            coVerify { repository.getInitialDataList(any()) }
            coVerify { viewState.showBookList(any()) }
            //saving in rep
            coVerify { repository.insertDataFromPathsInDb(any()) }
            //build complete list
            coVerify { repository.getCompleteDataList() }
            coVerify { viewState.showBookList(any()) }
            //loading end
            coVerify { viewState.updateLoadingStateView(any(), any(), any()) }
            coVerify { viewState.moveLoadingStateViewDown(any()) }
            coVerify { viewState.hideLoadingStateView() }
            confirmVerified(*getUsedMocks())
        }
*/
    @ExperimentalTime
    @Test
    fun `onOptionsMenuItemSelected should showSideMenu`() {
        presenter.onOptionsMenuItemSelected()

        verify { viewState.showSideMenu() }
        confirmVerified(*getUsedMocks())
    }

    @ExperimentalTime
    @Test
    fun `onActivityResult should not call mocks`() {
        confirmVerified(*getUsedMocks())
    }

    @ExperimentalTime
    @Test
    fun `onBookDismiss should do nothing when list isn't initialized`() {
        presenter.onBookDismiss(0)
        confirmVerified(*getUsedMocks())
    }

    @ExperimentalTime
    @Test
    fun `onBookMove should not call mocks`() {
        presenter.onBookMove(0, 1)
        confirmVerified(*getUsedMocks())
    }

    @ExperimentalTime
    @Test
    fun `onBookClicked should do nothing when list isn't initialized`() {
        presenter.onBookClicked(0)
        confirmVerified(*getUsedMocks())
    }

    open class Car(){
        private var x : Int = 0
        var property = 0

        private fun accelerate() : String = " 123"


        fun foo(){
            println(accelerate())
        }
    }

    @ExperimentalTime
    @Test
    fun `just test`() {
        val mock = spyk(Car(), recordPrivateCalls = true)

        every { mock["accelerate"]() } returns "going not so fast"
        mock.foo()
    }

    @ExperimentalTime
    @Test
    fun `onStop should do nothing when list isn't initialized`() {
        presenter.onStop()
        confirmVerified(*getUsedMocks())
    }

}
