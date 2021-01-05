package team.weathy.util

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.View.OnTouchListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.SpringAnimation
import team.weathy.databinding.ItemLocationBinding
import kotlin.math.abs

class LocationItemMenuSwiper private constructor(
    private val binding: ItemLocationBinding, private val callback: Callback
) : OnTouchListener {
    private var initialTranslateX = 0f
    private var offsetX = 0f
    private var springAnim: SpringAnimation? = null

    private var isOpened = false

    private val root = binding.root
    private val deleteContainer = binding.deleteContainer
    private val contentContainer = binding.contentContainer

    private var tracker: VelocityTracker? = null

    @SuppressLint("Recycle")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                tracker?.clear()
                tracker = tracker ?: VelocityTracker.obtain()
                tracker?.addMovement(event)

                springAnim?.cancel()

                initialTranslateX = contentContainer.translationX
                offsetX = event.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                tracker?.apply {
                    addMovement(event)
                    computeCurrentVelocity(1000)
                }

                contentContainer.translationX = initialTranslateX + event.rawX - offsetX
                updateDeleteContainer()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                val velocity = tracker?.xVelocity ?: 0f
                when {
                    abs(velocity) < abs(tracker!!.yVelocity) -> {
                    }
                    velocity >= 0 -> closeMenu()
                    else -> if (isOpened) deleteMenu() else openMenu()
                }

                tracker?.also {
                    it.recycle()
                    tracker = null
                }
            }
        }

        return true
    }

    fun openMenu(withAnim: Boolean = true) {
        isOpened = true
        val finalValue = -root.width / 2f

        if (withAnim) {
            springAnim = AnimUtil.runSpringAnimation(contentContainer.translationX, finalValue) {
                contentContainer.translationX = it
                updateDeleteContainer()
            }
        } else {
            contentContainer.translationX = finalValue
            updateDeleteContainer()
        }
        callback.onOpened()
    }

    fun closeMenu(withAnim: Boolean = true) {
        isOpened = false
        val finalValue = 0f

        if (withAnim) {
            springAnim = AnimUtil.runSpringAnimation(contentContainer.translationX, finalValue) {
                contentContainer.translationX = it
                updateDeleteContainer()
            }
        } else {
            contentContainer.translationX = finalValue
            updateDeleteContainer()
        }
        callback.onClosed()
    }

    fun deleteMenu() {
        isOpened = true
        springAnim = AnimUtil.runSpringAnimation(contentContainer.translationX, -root.width * 1.5f) {
            contentContainer.translationX = it
            updateDeleteContainer()
        }
        callback.onDeleted()
    }

    private fun updateDeleteContainer() {
        deleteContainer.alpha = -contentContainer.translationX / root.width * 2
        deleteContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            width = (-contentContainer.translationX.toInt() - 15.dp).coerceAtLeast(32.dp)
        }
    }

    interface Callback {
        fun onOpened()
        fun onClosed()
        fun onDeleted()
    }

    companion object {
        fun configure(binding: ItemLocationBinding, callback: Callback): LocationItemMenuSwiper {
            val listener = LocationItemMenuSwiper(binding, callback)
            binding.root.setOnTouchListener(listener)
            return listener
        }
    }
}