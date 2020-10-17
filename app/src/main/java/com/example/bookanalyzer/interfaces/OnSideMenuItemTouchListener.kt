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
                onActionDown(v, startColor, pressedColor)
                return (true)
            }
            MotionEvent.ACTION_CANCEL -> {
                onActionCancel(v, startColor, pressedColor)
                return (true)
            }
            MotionEvent.ACTION_UP -> {
                onActionUp(v, startColor, pressedColor)
                return (true)
            }
        }
        return (false)
    }

    private fun onActionDown(view:View?, startColor:Int, pressedColor:Int){
        createValueAnimator(view, startColor, pressedColor).start()
    }

    private fun onActionCancel(view:View?, startColor:Int, pressedColor:Int){
        createValueAnimator(view, pressedColor, startColor).start()
    }

    private fun onActionUp(view:View?, startColor:Int, pressedColor:Int){
        createValueAnimator(view, startColor, pressedColor).start()
        doAction()
        createValueAnimator(view, pressedColor, startColor).start()
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

