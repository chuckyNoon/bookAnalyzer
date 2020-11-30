package com.example.bookanalyzer.ui.adapters.side_menu_adapter

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.View
import com.example.bookanalyzer.R

abstract class OnSideMenuItemTouchListener : View.OnTouchListener {

    abstract fun onClick()

    private var startColor: Int? = null
    private var pressedColor: Int? = null

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v == null)
            return false

        val background = v.background as? ColorDrawable
        if (startColor == null) startColor = background?.color ?: Color.WHITE
        if (pressedColor == null) pressedColor = v.resources.getColor(R.color.purple)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                onActionDown(v)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                onActionCancel(v)
                return true
            }
            MotionEvent.ACTION_UP -> {
                onActionUp(v)
                return true
            }
        }
        return false
    }

    private fun onActionDown(view: View?) {
        createValueAnimator(view, startColor, pressedColor).start()
    }

    private fun onActionCancel(view: View?) {
        createValueAnimator(view, pressedColor, startColor).start()
    }

    private fun onActionUp(view: View?) {
        createValueAnimator(view, startColor, pressedColor).start()
        onClick()
        createValueAnimator(view, pressedColor, startColor).start()
    }

    private fun createValueAnimator(view: View?, colorFrom: Int?, colorTo: Int?) =
        ValueAnimator.ofObject(
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

