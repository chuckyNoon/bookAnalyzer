package com.example.bookanalyzer

import android.Manifest
import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.concurrent.thread

interface ISelectedLaunch{
    fun onSelectedLaunch(ifScan: Boolean)
}

interface ISelectedSearchSettings {
    fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File)
}

class StartActivity : AppCompatActivity(), ISelectedSearchSettings, ISelectedLaunch{
    private lateinit var listView: RecyclerView
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var bookList:ArrayList<ABookInfo>
    private lateinit var loadingStateTextView:TextView
    private var newAdapter:RecyclerListAdapter?= null
    private val handler = Handler()
    private var isListCreating = false
    private var pathSaver:PathSaver = PathSaver(this)
    private var mItemTouchHelper:ItemTouchHelper?=null

    private fun initFields(){
        listView = findViewById(R.id.list_view)
        drawerLayout = findViewById(R.id.drawerLayout)
        loadingStateTextView = findViewById(R.id.textview_loading_state)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        initFields()
        setToolBar()
        setSideMenu()

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
            createBookList()
        }
    }

    private fun setToolBar(){
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.products_toolbar)
        toolBar.title = "Files"
        toolBar.setNavigationIcon(R.drawable.baseline_menu_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home ){
            drawerLayout.open()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        val menuContentLoader = MenuContentLoader(this)
        for (book in bookList){
            val newWordCount = menuContentLoader.searchSavedWordCount(book.path)
            if (book.wordCount != newWordCount){
                book.wordCount = newWordCount
                newAdapter?.notifyDataSetChanged()
            }
        }
        super.onRestart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val filePath: String? = FileUtils().getPath(this, data!!.data!!)
            filePath?.let{
                bookList.add(MenuContentLoader(this).getDetailedInfo(filePath))
                newAdapter?.notifyItemInserted((bookList.size ?: 0) - 1)
                newAdapter?.notifyDataSetChanged()
                thread {
                    pathSaver.addPath(filePath)
                }
                println("ok $filePath")
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
        if (requestCode == 0) {
            SearchSettingsDialog().show(supportFragmentManager, "124")
        }
    }

    override fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File) {
        thread {
            val paths = BookSearch.findAll(dir, formats)
            pathSaver.saveAll(paths)
            handler.post {
                createBookList()
            }
        }
    }

    private fun hideLoadingStateTextView(dur: Long){
        val height = loadingStateTextView.height.toFloat()
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", height).apply{
            duration = dur
            start()
        }
    }

    private fun showLoadingStateTextView(dur:Long){
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", 0f).apply {
            duration = dur
            start()
        }
    }

    private fun updateLoadingStateTextView(str: String, downDuration: Long, upDuration: Long){
        val height = loadingStateTextView.height.toFloat()
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", height).apply {
            duration = downDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    loadingStateTextView.text = str
                    showLoadingStateTextView(upDuration)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            start()
        }
    }

    private fun createBookList(){
        isListCreating = true

        thread {
            val menuContentLoader = MenuContentLoader(this)
            createPrimaryList(menuContentLoader)
            addMoreInfoToPrimaryList(menuContentLoader)
            isListCreating = false
        }
    }

    private fun createPrimaryList(menuContentLoader:MenuContentLoader){
        if (newAdapter != null){
            val oldSize = bookList.size
            bookList.clear()
            newAdapter?.notifyItemRangeRemoved(0, oldSize)
            bookList.addAll(menuContentLoader.firstStage())
            newAdapter?.notifyItemRangeInserted(0, bookList.size)
        }else{
            bookList = menuContentLoader.firstStage()
            newAdapter = RecyclerListAdapter(this,bookList)
            handler.post{
                listView.setHasFixedSize(true)
                val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(newAdapter!!)
                mItemTouchHelper = ItemTouchHelper(callback)
                mItemTouchHelper?.attachToRecyclerView(listView)

                listView.adapter = newAdapter
                listView.layoutManager = LinearLayoutManager(this)

            }
        }
        handler.post {
            showLoadingStateTextView(400)
            loadingStateTextView.visibility = View.VISIBLE
            loadingStateTextView.text = "Loading content"
        }
    }

    private fun addMoreInfoToPrimaryList(menuContentLoader:MenuContentLoader){
        for (i in bookList.indices ) {
            val oldElem = bookList[i]
            val newElem = menuContentLoader.getDetailedInfo(oldElem.path)

            oldElem.name = newElem.name
            oldElem.author = newElem.author
            oldElem.bitmap = newElem.bitmap
            oldElem.wordCount = newElem.wordCount
            handler.post {
                newAdapter?.notifyDataSetChanged()
            }
        }

        handler.post{
            updateLoadingStateTextView("Loading ended", 400, 400)
        }
        handler.postDelayed({hideLoadingStateTextView(400)}, 3000)
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
                    if (!isListCreating) {
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

class FileUtils() {
    private var contentUri: Uri? = null

    fun getPath(context: Context, uri: Uri): String? {
        // check here to KITKAT or new version
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                val fullPath = getPathFromExtSD(split)
                return if (fullPath !== "") {
                    fullPath
                } else {
                    null
                }
            } else if (isDownloadsDocument(uri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val id: String
                    var cursor: Cursor? = null
                    try {
                        cursor = context.contentResolver.query(
                            uri,
                            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                            null,
                            null,
                            null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
                            val fileName = cursor.getString(0)
                            val path =
                                Environment.getExternalStorageDirectory()
                                    .toString() + "/Download/" + fileName
                            if (!TextUtils.isEmpty(path)) {
                                return path
                            }
                        }
                    } finally {
                        cursor?.close()
                    }
                    id = DocumentsContract.getDocumentId(uri)
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:".toRegex(), "")
                        }
                        val contentUriPrefixesToTry =
                            arrayOf(
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                            )
                        for (contentUriPrefix in contentUriPrefixesToTry) {
                            return try {
                                val contentUri = ContentUris.withAppendedId(
                                    Uri.parse(contentUriPrefix),
                                    java.lang.Long.valueOf(id)
                                )

                                /*   final Uri contentUri = ContentUris.withAppendedId(
                                                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));*/
                                getDataColumn(context, contentUri, null, null)
                            } catch (e: NumberFormatException) {
                                //In Android 8 and Android P the id is not a number
                                uri.path!!.replaceFirst("^/document/raw:".toRegex(), "")
                                    .replaceFirst("^raw:".toRegex(), "")
                            }
                        }
                    }
                } else {
                    val id = DocumentsContract.getDocumentId(uri)
                    val isOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:".toRegex(), "")
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                    if (contentUri != null) {
                        return getDataColumn(context, contentUri, null, null)
                    }
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
                return getDataColumn(
                    context, contentUri, selection,
                    selectionArgs
                )
            } else if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            }
            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context)
            }
            return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                // return getFilePathFromURI(context,uri);
                getMediaFilePathForN(uri, context)
                // return getRealPathFromURI(context,uri);
            } else {
                getDataColumn(context, uri, null, null)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Check if a file exists on device
     *
     * @param filePath The absolute file path
     */
    private fun fileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    /**
     * Get full file path from external storage
     *
     * @param pathData The storage type and the relative path
     */
    private fun getPathFromExtSD(pathData: Array<String>): String {
        val type = pathData[0]
        val relativePath = "/" + pathData[1]
        var fullPath = ""

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equals(type, ignoreCase = true)) {
            fullPath =
                Environment.getExternalStorageDirectory().toString() + relativePath
            if (fileExists(fullPath)) {
                return fullPath
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath
        if (fileExists(fullPath)) {
            return fullPath
        }
        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath
        return if (fileExists(fullPath)) {
            fullPath
        } else fullPath
    }

    private fun getDriveFilePath(
        uri: Uri,
        context: Context
    ): String {
        val returnCursor =
            context.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.cacheDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also({ read = it }) != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {

        }
        return file.getPath()
    }

    private fun getMediaFilePathForN(
        uri: Uri,
        context: Context
    ): String {
        val returnCursor =
            context.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.filesDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also({ read = it }) != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
        }
        return file.getPath()
    }

    private fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Drive.
     */
    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }
}

