package com.example.bookanalyzer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlin.concurrent.thread


class StartActivity : AppCompatActivity() {
    private lateinit var listView: RecyclerView
    private lateinit var menuContentLoader: MenuContentLoader
    private var drawerLayout:DrawerLayout?=null
    private var myAdapter: MyAdapter? = null
    private var bookCount = 0
    private lateinit var bookList:ArrayList<ABookInfo>
    private val handler = Handler()
    //private var loadingToast:Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_start)
        setToolBar()
        setSideMenu()

        menuContentLoader = MenuContentLoader(this)
        listView = findViewById(R.id.list_view)
        drawerLayout = findViewById(R.id.drawerLayout)

        val requiredPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, requiredPermissions, 0)
    }

    private fun setSideMenu(){
        val ar = ArrayList<SideMenuElemModel>()
        ar.add(SideMenuElemModel("",null, View.OnClickListener{
            println("11")
        }))
        ar.add(SideMenuElemModel("Select new file...", R.drawable.baseline_folder_24, View.OnClickListener{
            println("1")
        }))
        ar.add(SideMenuElemModel("Search files...", R.drawable.baseline_search_24, View.OnClickListener{
            println("2")
        }))
        val sideMenuAdapter = SideMenuAdapter(this, ar)
        findViewById<ListView>(R.id.sideMenuListView).adapter = sideMenuAdapter
    }

    private fun setToolBar(){
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.products_toolbar)
        toolBar.title = "Files"
        toolBar.setNavigationIcon(R.drawable.baseline_menu_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home ){
            drawerLayout?.open()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            val f = Environment.getExternalStorageDirectory()
            thread {
                val paths = BookSearch.findAll(f!!)
                val time1 = System.currentTimeMillis()
                bookList = menuContentLoader.firstStage(paths)
                handler.post{
                    myAdapter = MyAdapter(this, bookList)
                    listView.adapter = myAdapter
                    listView.layoutManager = LinearLayoutManager(this);
                }
                for (i in bookList.indices) {
                    val oldElem = bookList[i]
                    val newElem = menuContentLoader.loadMoreInfo(paths[i])
                    oldElem.name = newElem.name
                    oldElem.author = newElem.author
                    oldElem.bitmap = newElem.bitmap
                    oldElem.wordCount = newElem.wordCount
                }
                val time2 = System.currentTimeMillis()
                println("f = " + (time2 - time1).toDouble() / 1000)
                handler.post{
                    myAdapter?.notifyDataSetChanged()
                   // loadingToast = Toast.makeText(this, "Download ended", Toast.LENGTH_LONG)
                  //  loadingToast?.show()
                    //loadingToast?.cancel()
                }
            }
        }
    }

    override fun onRestart() {
        for (book in bookList){
            val newWordCount = menuContentLoader.searchSavedWordCount(book.path)
            if (book.wordCount != newWordCount){
                book.wordCount = newWordCount
                myAdapter?.notifyDataSetChanged()
            }
        }
        super.onRestart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val strUri = data?.data.toString()
            val intent = Intent(this, LoaderActivity::class.java)
            val newBookInd = bookCount
            intent.putExtra("uri", strUri)
            intent.putExtra("ind", newBookInd)
            startActivity(intent)
        }
    }
}
