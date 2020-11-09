package com.example.bookanalyzer

import android.content.Context
import javax.inject.Inject

class ResourceManager @Inject constructor(private val res: Context) {
    fun getString(id: Int) = res.getString(id)
}