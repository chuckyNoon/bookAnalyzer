package com.example.bookanalyzer.ui.activities

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.example.bookanalyzer.interfaces.OnSideMenuItemTouchListener
import com.example.bookanalyzer.ui.adapters.SideMenuAdapter
import com.example.bookanalyzer.ui.fragments.SearchSettingsDialog
import com.example.bookanalyzer.interfaces.SimpleItemTouchHelperCallback
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.example.bookanalyzer.mvp.repositories.StartScreenRepository
import com.example.bookanalyzer.mvp.views.StartView
import com.example.bookanalyzer.ui.adapters.BookListAdapter
import com.example.bookanalyzer.ui.adapters.SideMenuItemModel
import com.example.bookanalyzer.ui.fragments.FirstLaunchDialog
import kotlinx.coroutines.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

import java.io.File

interface ISelectedLaunch{
    fun onSelectedLaunch(ifScan: Boolean)
}

interface ISelectedSearchSettings  {
    fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File)
}

class StartActivity : MvpAppCompatActivity(), ISelectedSearchSettings, ISelectedLaunch, StartView{
    private lateinit var listView: RecyclerView
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var loadingStateTextView:TextView
    private lateinit var adapter: BookListAdapter
    private lateinit var sideMenuListView:ListView

    private var repository = StartScreenRepository(this)
    private val presenter by moxyPresenter { StartScreenPresenter(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        initFields()
        setToolBar()
        setSideMenu()
        setRecyclerView()

        val prefs = getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
        if (!prefs.contains("firstLaunch")) {
            prefs.edit().putBoolean("firstLaunch", true).apply()
            FirstLaunchDialog().show(supportFragmentManager, "123")
        }else{
            if (savedInstanceState == null)
                presenter.onViewCreated()
        }
    }

    private fun initFields(){
        listView = findViewById(R.id.list_view)
        drawerLayout = findViewById(R.id.drawerLayout)
        loadingStateTextView = findViewById(R.id.textview_loading_state)
        sideMenuListView = findViewById(R.id.sideMenuListView)
    }

    private fun setToolBar(){
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.products_toolbar)
        toolBar.title = "Files"
        toolBar.setNavigationIcon(R.drawable.baseline_menu_24)
        setSupportActionBar(toolBar)
    }

    private fun setRecyclerView() {
        val defaultBookImage = BitmapFactory.decodeResource(this.resources, R.drawable.book)
        adapter = BookListAdapter(defaultBookImage, presenter)
        listView.setHasFixedSize(true)
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(presenter)
        val mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(listView)

        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home ){
            presenter.onOptionsItemSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        presenter.onRestart()

    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun startLoadingActivity(bookPath:String, newBookInd:Int) {
        val intent = Intent(this, LoaderScreenActivity::class.java)
        intent.putExtra("path", bookPath)
        intent.putExtra("ind", newBookInd)
        startActivity(intent)
    }

    override fun startInfoActivity(bookInd:Int) {
        val intentToBook = Intent(this, BookInfoActivity::class.java)
        intentToBook.putExtra("ind", bookInd)
        startActivity(intentToBook)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val bookPath = withContext(Dispatchers.IO) {
                    FileUtils().getPath(this@StartActivity, data!!.data!!)
                }
                bookPath?.let{
                    presenter.onActivityResult(bookPath)
                }
            }
        }
    }

    override fun onSelectedLaunch(ifScan: Boolean) {
        if (ifScan){
            val requiredPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, requiredPermissions, 0)
        }else{
            Toast.makeText(this, "Books not searched", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    override fun showSearchSettingsDialog() {
        SearchSettingsDialog().show(supportFragmentManager, "124")
    }

    override fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File) {
        presenter.onSelectedSearchSettings(formats, dir)
    }

    override fun hideLoadingStateView(){
        loadingStateTextView.visibility = View.INVISIBLE
    }

    override fun moveLoadingStateViewUp(dur:Int){
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", 0f).apply{
            duration = dur.toLong()
            start()
        }
    }

    override fun moveLoadingStateViewDown(dur:Int){
        val height = loadingStateTextView.height.toFloat()
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", height).apply{
            duration = dur.toLong()
            start()
        }
    }

    override fun showLoadingStateView(){
        loadingStateTextView.visibility = View.VISIBLE
    }

    override fun setLoadingStateViewText(text: String) {
        loadingStateTextView.text = text
    }

    override fun updateLoadingStateView(str: String, downDuration: Long, upDuration: Long){
        val height = loadingStateTextView.height.toFloat()
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", height).apply {
            duration = downDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }
                override fun onAnimationEnd(animation: Animator?) {
                    loadingStateTextView.text = str
                    ObjectAnimator.ofFloat(loadingStateTextView, "translationY", 0f).apply {
                        duration = upDuration
                        start()
                    }
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

    override fun showList(bookList: ArrayList<MenuBookModel>) {
        adapter.setupBooks(bookList)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setSideMenu(){
        val ar = ArrayList<SideMenuItemModel>()
        ar.add(SideMenuItemModel(
            "",
            null,
            View.OnTouchListener { view: View, motionEvent: MotionEvent ->
                false
            }
        ))
        ar.add(SideMenuItemModel(
            "Select new file...",
            R.drawable.baseline_folder_24,
            object : OnSideMenuItemTouchListener {
                override fun doAction() {
                    drawerLayout.close()
                    val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*")
                    startActivityForResult(intent, 123)
                }
            }
        ))

        ar.add(SideMenuItemModel(
            "Search files...",
            R.drawable.baseline_search_24,
            object : OnSideMenuItemTouchListener {
                override fun doAction() {
                    drawerLayout.close()
                    SearchSettingsDialog().show(supportFragmentManager, "124")
                }
            }
        ))
        val sideMenuAdapter = SideMenuAdapter(this, ar)
        sideMenuListView.adapter = sideMenuAdapter
    }
}

