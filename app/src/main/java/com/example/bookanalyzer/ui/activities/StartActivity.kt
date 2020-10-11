package com.example.bookanalyzer.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.bookanalyzer.R
import com.example.bookanalyzer.common.FileUtils
import com.example.bookanalyzer.interfaces.OnSideMenuItemTouchListener
import com.example.bookanalyzer.ui.adapters.SideMenuAdapter
import com.example.bookanalyzer.ui.adapters.SideMenuItemModel
import com.example.bookanalyzer.ui.fragments.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpAppCompatActivity
import java.io.File

interface ISelectedLaunch{
    fun onSelectedLaunch(ifScan: Boolean)
}

interface ISelectedSearchSettings  {
    fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File)
}

interface IOnBookClicked{
    fun onAnalyzedBookClicked(ind: Int)
    fun onNewBookClicked(path: String, newInd: Int)
}

interface IOnLoadingCompleted{
    fun onLoadingCompleted(ind: Int)
}

class StartActivity :MvpAppCompatActivity(),ISelectedSearchSettings,
    ISelectedLaunch, IOnBookClicked, IOnLoadingCompleted{
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var sideMenuListView:ListView
    private lateinit var toolbar:  androidx.appcompat.widget.Toolbar
    private var frameLayout:FrameLayout?=null
    private var rightFragment:Fragment?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        initFields()
        setToolBar()
        setSideMenu()
        val prefs = getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

        if (!prefs.contains("firstLaunch")) {
            prefs.edit().putBoolean("firstLaunch", true).apply()
            FirstLaunchDialog().show(supportFragmentManager, "123")
        }else if(savedInstanceState == null){
            val bookListFragment = supportFragmentManager.findFragmentById(R.id.fragment4) as BookListFragment
            bookListFragment.loadContentInFragment()
        }
        val bookListFragment = supportFragmentManager.findFragmentById(R.id.fragment4) as BookListFragment
        bookListFragment.loadMode(if (frameLayout == null)0 else 1)
    }

    private fun initFields(){
        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        sideMenuListView = findViewById(R.id.sideMenuListView)
        frameLayout = findViewById(R.id.frame)
    }

    private fun setToolBar(){
        toolbar.title = "Files"
        toolbar.setNavigationIcon(R.drawable.baseline_menu_24)
        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home )
            showSideMenu()
        return super.onOptionsItemSelected(item)
    }

    private fun showSideMenu() {
        drawerLayout.open()
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
                    val f = supportFragmentManager.findFragmentById(R.id.fragment4) as BookListFragment
                    f.onResult(bookPath)
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
        SearchSettingsDialog().show(supportFragmentManager, "124")
    }

    override fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File){
        val f = supportFragmentManager.findFragmentById(R.id.fragment4) as BookListFragment
        f.onSelectedSearchSettings(formats, dir)
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
                    val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("* / *")
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

    override fun onAnalyzedBookClicked(ind: Int) {
        if (frameLayout != null) {
            val f = BookInfoFragment.newInstance(ind)
            replaceFragment(f)
        }
    }

    override fun onNewBookClicked(path: String, newInd: Int) {
        if (frameLayout != null){
            val f = LoaderScreenFragment.newInstance(path, newInd)
            replaceFragment(f)
        }
    }

    private fun replaceFragment(f: Fragment){
        supportFragmentManager.popBackStack()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null).replace(R.id.frame, f)
        transaction.commit()
        rightFragment = f
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0){
            rightFragment = null
        }
    }

    override fun onLoadingCompleted(ind: Int) {
        onAnalyzedBookClicked(ind)
        val f = supportFragmentManager.findFragmentById(R.id.fragment4) as BookListFragment
        f.updateWordCount()
    }
}
