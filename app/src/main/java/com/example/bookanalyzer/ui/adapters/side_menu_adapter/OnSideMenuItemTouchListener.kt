package com.example.bookanalyzer.ui.adapters.side_menu_adapter

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import com.example.bookanalyzer.R

interface OnSideMenuItemTouchListener : View.OnTouchListener {

    fun onClick()

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v == null)
            return false
        val startColor = v.resources.getColor(R.color.colorMenuItemDefault)
        val pressedColor = v.resources.getColor(R.color.colorMenuItemPressed)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                onActionDown(v, startColor, pressedColor)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                onActionCancel(v, startColor, pressedColor)
                return true
            }
            MotionEvent.ACTION_UP -> {
                onActionUp(v, startColor, pressedColor)
                return true
            }
        }
        return false
    }

    private fun onActionDown(view: View?, startColor: Int, pressedColor: Int) {
        createValueAnimator(view, startColor, pressedColor).start()
    }

    private fun onActionCancel(view: View?, startColor: Int, pressedColor: Int) {
        createValueAnimator(view, pressedColor, startColor).start()
    }

    private fun onActionUp(view: View?, startColor: Int, pressedColor: Int) {
        createValueAnimator(view, startColor, pressedColor).start()
        onClick()
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

