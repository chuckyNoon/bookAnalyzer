package com.example.bookanalyzer.interfaces

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.ListView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ui.adapters.SideMenuAdapter
import com.example.bookanalyzer.ui.adapters.SideMenuItemModel
import com.example.bookanalyzer.ui.fragments.SearchSettingsDialog

interface OnSideMenuItemTouchListener : View.OnTouchListener {
    fun doAction()

    private fun createValueAnimator(v: View?, colorFrom: Int, colorTo: Int) : ValueAnimator {
        return ValueAnimator.ofObject(
            ArgbEvaluator(),
            colorFrom,
            colorTo
        ).apply {
            duration = 250
            addUpdateListener { animator ->
                v?.setBackgroundColor(animator.animatedValue as Int)
            }
        }
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

