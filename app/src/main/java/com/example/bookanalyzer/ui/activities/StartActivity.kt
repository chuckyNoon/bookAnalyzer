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
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.*
import com.example.bookanalyzer.common.FileUtils
import com.example.bookanalyzer.ui.adapters.OnSideMenuItemTouchListener
import com.example.bookanalyzer.ui.adapters.SideMenuAdapter
import com.example.bookanalyzer.ui.fragments.SearchSettingsDialog
import com.example.bookanalyzer.ui.adapters.SimpleItemTouchHelperCallback
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.mvp.views.StartView
import com.example.bookanalyzer.ui.adapters.BookItemsAdapter
import com.example.bookanalyzer.ui.adapters.BookItem
import com.example.bookanalyzer.ui.adapters.SideMenuItem
import com.example.bookanalyzer.ui.fragments.FirstLaunchDialog
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import java.io.File
import javax.inject.Inject

const val EXTRA_BOOK_PATH = "BookPath"
const val EXTRA_ANALYSIS_ID = "AnalysisId"

class StartActivity : MvpAppCompatActivity(), SearchSettingsDialog.OnSelectedSearchSettings,
    FirstLaunchDialog.OnSelectedLaunchOption, StartView {

    companion object {
        private const val PREFERENCES_TAG = "APP_PREFERENCES"
        private const val FIRST_LAUNCH_TAG = "firstLaunch"
        private const val SEARCH_SETTINGS_DIALOG_TAG = "124"
        private const val REQUEST_PERMISSION_FOR_BOOK_ADD_CODE = 1
        private const val REQUEST_PERMISSION_FOR_BOOK_SEARCH_CODE = 2
        private const val SELECT_FILE_REQUEST_CODE = 123
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var loadingStateTextView: TextView
    private lateinit var adapter: BookItemsAdapter
    private lateinit var sideMenuListView: ListView
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar

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
        setContentView(R.layout.activity_start)

        initFields()
        initToolBar()
        initSideMenu()
        initRecyclerView()
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

    private fun initFields() {
        recyclerView = findViewById(R.id.recycler_view_books)
        drawerLayout = findViewById(R.id.drawerLayout)
        loadingStateTextView = findViewById(R.id.text_view_loading_state)
        sideMenuListView = findViewById(R.id.sideMenuListView)
        toolBar = findViewById(R.id.toolbar)
    }

    private fun initToolBar() {
        toolBar.title = resources.getString(R.string.start_screen_title)
        toolBar.setNavigationIcon(R.drawable.baseline_menu_24)
        setSupportActionBar(toolBar)
    }

    private fun initRecyclerView() {
        adapter = BookItemsAdapter(this, presenter)
        recyclerView.setHasFixedSize(true)
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(presenter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
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
                val bookPath = FileUtils().getPathByUri(this@StartActivity, uri)
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
            drawerLayout.close()
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
        loadingStateTextView.visibility = View.VISIBLE
    }

    override fun hideLoadingStateView() {
        loadingStateTextView.visibility = View.INVISIBLE
    }

    override fun moveLoadingStateViewUp(animDuration: Int) {
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", 0f).apply {
            this.duration = animDuration.toLong()
            start()
        }
    }

    override fun moveLoadingStateViewDown(animDuration: Int) {
        val height = loadingStateTextView.height.toFloat()
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", height).apply {
            this.duration = animDuration.toLong()
            start()
        }
    }

    override fun setLoadingStateViewText(text: String) {
        loadingStateTextView.text = text
    }

    override fun updateLoadingStateView(
        text: String,
        animDownDuration: Long,
        animUpDuration: Long
    ) {
        val height = loadingStateTextView.height.toFloat()

        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", height).apply {
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
        drawerLayout.open()
    }

    override fun showBookList(bookItems: ArrayList<BookItem>) {
        adapter.setupBooks(bookItems)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSideMenu() {
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
        sideMenuListView.adapter = sideMenuAdapter
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

