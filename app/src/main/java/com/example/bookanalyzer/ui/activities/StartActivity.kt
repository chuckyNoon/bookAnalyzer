package com.example.bookanalyzer.ui.activities

import android.Manifest
import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.*
import com.example.bookanalyzer.ui.adapters.RecyclerListAdapter
import com.example.bookanalyzer.ui.adapters.SideMenuAdapter
import com.example.bookanalyzer.ui.adapters.SideMenuElemModel
import com.example.bookanalyzer.ui.fragments.SearchSettingsDialog
import com.example.bookanalyzer.interfaces.SimpleItemTouchHelperCallback
import com.example.bookanalyzer.mvp.presenters.BookInfoPresenter
import com.example.bookanalyzer.mvp.presenters.StartActivityPresenter
import com.example.bookanalyzer.mvp.views.StartView
import com.example.bookanalyzer.ui.fragments.FirstLaunchDialog
import java.io.File

interface ISelectedLaunch{
    fun onSelectedLaunch(ifScan: Boolean)
}

interface ISelectedSearchSettings {
    fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File)
}

class StartActivity : AppCompatActivity(), ISelectedSearchSettings, ISelectedLaunch, StartView{
    private lateinit var listView: RecyclerView
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var loadingStateTextView:TextView

    private lateinit var presenter: StartActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        initFields()
        setToolBar()
        setSideMenu()
        presenter = StartActivityPresenter(this, this.applicationContext)

        /*val a = TypedValue()
        theme.resolveAttribute(android.R.attr.windowBackground, a, true)
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            val color: Int = a.data
            val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and color)
            println("w " + hexColor)
        } else {
            // windowBackground is not a color, probably a drawable
            val d: Drawable = getResources().getDrawable(a.resourceId)
        }*/

        val prefs = getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
        if (!prefs.contains("firstLaunch")) {
            prefs.edit().putBoolean("firstLaunch", true).apply()
            FirstLaunchDialog().show(supportFragmentManager, "123")
        }else{
            presenter.createBookList()
        }
    }

    override fun initRecyclerView(adapter: RecyclerListAdapter) {
        listView.setHasFixedSize(true)
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        val mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(listView)

        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(this)
    }

    private fun initFields(){
        listView = findViewById(R.id.list_view)
        drawerLayout = findViewById(R.id.drawerLayout)
        loadingStateTextView = findViewById(R.id.textview_loading_state)
    }

    private fun setToolBar(){
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.products_toolbar)
        toolBar.title = "Files"
        toolBar.setNavigationIcon(R.drawable.baseline_menu_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        presenter.onRestart()
        super.onRestart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode,resultCode, data)
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
      /*  val height = loadingStateTextView.height.toFloat()
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", height).apply{
            duration = dur
            start()
        }*/
        loadingStateTextView.visibility = View.INVISIBLE
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

    interface OnSideMenuElemTouchListener : View.OnTouchListener {
        fun doAction()

        private fun createValueAnimator(v: View?, colorFrom: Int, colorTo: Int) : ValueAnimator {
            val colorAnimation: ValueAnimator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                colorFrom,
                colorTo
            )
            colorAnimation.duration = 250 // milliseconds

            colorAnimation.addUpdateListener { animator ->
                v?.setBackgroundColor(animator.animatedValue as Int)
            }
            return colorAnimation
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val defColor = Color.parseColor("#303030")
            val pressedColor = Color.parseColor("#AB84F2")

            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    createValueAnimator(v, defColor, pressedColor).start()
                    return true
                }
                MotionEvent.ACTION_CANCEL -> {
                    createValueAnimator(v, pressedColor, defColor).start()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    createValueAnimator(v, defColor, pressedColor).start()
                    doAction()
                    createValueAnimator(v, pressedColor, defColor).start()
                    return true
                }
            }
            return false
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    private fun setSideMenu(){
        val ar = ArrayList<SideMenuElemModel>()
        ar.add(SideMenuElemModel(
            "",
            null,
            View.OnTouchListener { view: View, motionEvent: MotionEvent ->
                false
            }
        ))
        ar.add(SideMenuElemModel(
            "Select new file...",
            R.drawable.baseline_folder_24,
            object : OnSideMenuElemTouchListener {
                override fun doAction() {
                    drawerLayout.close()
                    val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*")
                    startActivityForResult(intent, 123)
                }
            }
        ))

        ar.add(SideMenuElemModel(
            "Search files...",
            R.drawable.baseline_search_24,
            object : OnSideMenuElemTouchListener {
                override fun doAction() {
                    if (true) {
                        drawerLayout.close()
                        SearchSettingsDialog().show(supportFragmentManager, "124")
                    }
                }
            }
        ))
        val sideMenuAdapter = SideMenuAdapter(this, ar)
        findViewById<ListView>(R.id.sideMenuListView).adapter = sideMenuAdapter
    }
}

