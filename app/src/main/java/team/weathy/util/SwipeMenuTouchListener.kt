package team.weathy.util

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.View.OnTouchListener

class SwipeMenuTouchListener(private val callback: Callback) : OnTouchListener {
    private var velocityTracker: VelocityTracker? = null

    private var dx = 0f

    @SuppressLint("Recycle")
    override fun onTouch(view: View, e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                velocityTracker?.clear()
                velocityTracker = velocityTracker ?: VelocityTracker.obtain()
                velocityTracker?.addMovement(e)

                dx = view.x - e.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.apply {
                    addMovement(e)
                    computeCurrentVelocity(1000)
                }

                callback.onTranslateXChanged(e.rawX + dx)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                val velocityX = velocityTracker?.xVelocity ?: 0f
                when {
                    velocityX >= 0 -> callback.onMenuClosed()
                    velocityX > -100f -> callback.onMenuOpened()
                    else -> callback.onDeleted()
                }
            }
        }

        return true
    }

    interface Callback {
        fun onTranslateXChanged(x: Float) {}
        fun onMenuOpened() {}
        fun onMenuClosed() {}
        fun onDeleted() {}
    }
}