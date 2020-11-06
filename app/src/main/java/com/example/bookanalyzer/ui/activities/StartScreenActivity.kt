package com.example.bookanalyzer.ui.activities

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookanalyzer.*
import com.example.bookanalyzer.common.FileUtils
import com.example.bookanalyzer.databinding.ActivityStartBinding
import com.example.bookanalyzer.ui.adapters.side_menu_adapter.OnSideMenuItemTouchListener
import com.example.bookanalyzer.ui.adapters.side_menu_adapter.SideMenuAdapter
import com.example.bookanalyzer.ui.fragments.SearchSettingsDialog
import com.example.bookanalyzer.ui.adapters.book_items_adapter.SimpleItemTouchHelperCallback
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.mvp.views.StartScreenView
import com.example.bookanalyzer.ui.adapters.book_items_adapter.BookItemsAdapter
import com.example.bookanalyzer.ui.adapters.book_items_adapter.BookItem
import com.example.bookanalyzer.ui.adapters.side_menu_adapter.SideMenuItem
import com.example.bookanalyzer.ui.fragments.FirstLaunchDialog
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import java.io.File
import javax.inject.Inject

const val EXTRA_BOOK_PATH = "BookPath"
const val EXTRA_ANALYSIS_ID = "AnalysisId"

class StartScreenActivity : MvpAppCompatActivity(), SearchSettingsDialog.OnSelectedSearchSettings,
    FirstLaunchDialog.OnSelectedLaunchOption, StartScreenView {

    companion object {
        private const val PREFERENCES_TAG = "APP_PREFERENCES"
        private const val FIRST_LAUNCH_TAG = "firstLaunch"
        private const val SEARCH_SETTINGS_DIALOG_TAG = "124"
        private const val REQUEST_PERMISSION_FOR_BOOK_ADD_CODE = 1
        private const val REQUEST_PERMISSION_FOR_BOOK_SEARCH_CODE = 2
        private const val SELECT_FILE_REQUEST_CODE = 123
    }

    private lateinit var adapter: BookItemsAdapter
    private lateinit var binding: ActivityStartBinding

    @Inject
    lateinit var repository: StartScreenRepository

    @InjectPresenter
    lateinit var presenter: StartScreenPresenter

    @ProvidePresenter
    fun provideStartScreenPresenter(): StartScreenPresenter {
        MyApp.appComponent.inject(this)
        return StartScreenPresenter(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolBar()
        setupSideMenu()
        setupRecyclerView()
        selectLaunchOption(savedInstanceState != null)
    }

    private fun isFirstApplicationLaunch(): Boolean {
        val prefs = getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE)
        return if (!prefs.contains(FIRST_LAUNCH_TAG)) {
            prefs.edit().putBoolean(FIRST_LAUNCH_TAG, true).apply()
            (true)
        } else {
            (false)
        }
    }

    private fun selectLaunchOption(isActivityRecreated: Boolean) {
        if (isFirstApplicationLaunch()) {
            FirstLaunchDialog().show(supportFragmentManager, "123")
        } else if (!isActivityRecreated) {
            presenter.onViewCreated()
        }
    }

    private fun setupToolBar() {
        binding.toolbar.title = resources.getString(R.string.start_screen_title)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_menu_24)
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        adapter = BookItemsAdapter(this, presenter)
        binding.books.setHasFixedSize(true)
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(presenter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.books)

        binding.books.adapter = adapter
        binding.books.layoutManager = LinearLayoutManager(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsMenuItemSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun startLoaderScreenActivity(bookPath: String) {
        val intent = Intent(this, LoaderScreenActivity::class.java).apply {
            putExtra(EXTRA_BOOK_PATH, bookPath)
        }
        startActivity(intent)
    }

    override fun startBookInfoActivity(analysisId: Int) {
        val intent = Intent(this, BookInfoActivity::class.java).apply {
            putExtra(EXTRA_ANALYSIS_ID, analysisId)
        }
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val bookPath = FileUtils().getPathByUri(this@StartScreenActivity, uri)
                bookPath?.let {
                    presenter.onActivityResult(bookPath)
                }
            }
        }
    }

    private fun requestReadPermission(requestCode: Int) {
        val requiredPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, requiredPermissions, requestCode)
    }

    override fun onSelectedLaunchOption(isScanSelected: Boolean) {
        if (isScanSelected) {
            requestReadPermission(REQUEST_PERMISSION_FOR_BOOK_SEARCH_CODE)
        } else {
            Toast.makeText(
                this,
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
            binding.drawerLayout.close()
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

    override fun showSearchSettingsDialog() {
        SearchSettingsDialog().show(supportFragmentManager, SEARCH_SETTINGS_DIALOG_TAG)
    }

    override fun onSelectedSearchSettings(bookFormats: ArrayList<String>, searchRootDir: File) {
        presenter.onSelectedSearchSettings(bookFormats, searchRootDir)
    }

    override fun showLoadingStateView() {
        binding.loadingStateView.visibility = View.VISIBLE
    }

    override fun hideLoadingStateView() {
        binding.loadingStateView.visibility = View.INVISIBLE
    }

    override fun moveLoadingStateViewUp(animDuration: Int) {
        ObjectAnimator.ofFloat(binding.loadingStateView, "translationY", 0f).apply {
            this.duration = animDuration.toLong()
            start()
        }
    }

    override fun moveLoadingStateViewDown(animDuration: Int) {
        val height = binding.loadingStateView.height.toFloat()
        ObjectAnimator.ofFloat(binding.loadingStateView, "translationY", height).apply {
            this.duration = animDuration.toLong()
            start()
        }
    }

    override fun setLoadingStateViewText(text: String) {
        binding.loadingStateView.text = text
    }

    override fun updateLoadingStateView(
        text: String,
        animDownDuration: Long,
        animUpDuration: Long
    ) {
        val height = binding.loadingStateView.height.toFloat()

        ObjectAnimator.ofFloat(binding.loadingStateView, "translationY", height).apply {
            duration = animDownDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    setLoadingStateViewText(text)
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

    override fun showSideMenu() {
        binding.drawerLayout.open()
    }

    override fun showBookList(bookItems: ArrayList<BookItem>) {
        adapter.setupBooks(bookItems)
    }

    //remake
    @SuppressLint("ClickableViewAccessibility")
    private fun setupSideMenu() {
        val sideMenuItemList = ArrayList<SideMenuItem>()
        sideMenuItemList.add(SideMenuItem(
            "",
            null,
            View.OnTouchListener { view: View, motionEvent: MotionEvent ->
                false
            }
        ))
        sideMenuItemList.add(SideMenuItem(
            resources.getString(R.string.select_new_file),
            R.drawable.baseline_folder_24,
            object : OnSideMenuItemTouchListener {
                override fun doAction() {
                    requestReadPermission(REQUEST_PERMISSION_FOR_BOOK_ADD_CODE)
                }
            }
        ))
        sideMenuItemList.add(SideMenuItem(
            resources.getString(R.string.search_books),
            R.drawable.baseline_search_24,
            object : OnSideMenuItemTouchListener {
                override fun doAction() {
                    requestReadPermission(REQUEST_PERMISSION_FOR_BOOK_SEARCH_CODE)
                }
            }
        ))
        val sideMenuAdapter = SideMenuAdapter(this, sideMenuItemList)
        binding.sideMenu.sideMenuListView.adapter = sideMenuAdapter
    }

    override fun onRestart() {
        super.onRestart()
        presenter.onRestart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }
}

