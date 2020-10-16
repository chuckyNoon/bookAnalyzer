package com.example.bookanalyzer.interfaces

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.MotionEvent
import android.view.View

interface OnSideMenuItemTouchListener : View.OnTouchListener {
    fun doAction()

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val startColor = Color.parseColor("#303030")
        val pressedColor = Color.parseColor("#AB84F2")

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                createValueAnimator(v, startColor, pressedColor).start()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                createValueAnimator(v, pressedColor, startColor).start()
                return true
            }
            MotionEvent.ACTION_UP -> {
                createValueAnimator(v, startColor, pressedColor).start()
                doAction()
                createValueAnimator(v, pressedColor, startColor).start()
                return true
            }
        }
        return false
    }

    private fun createValueAnimator(view: View?, colorFrom: Int, colorTo: Int): ValueAnimator {
        return ValueAnimator.ofObject(
            ArgbEvaluator(),
            colorFrom,
            colorTo
        ).apply {
            duration = 250
            addUpdateListener { animator ->
                view?.setBackgroundColor(animator.animatedValue as Int)
            }
        }
    }
}

