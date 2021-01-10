package team.weathy.util

import android.animation.AnimatorInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import team.weathy.R

@BindingAdapter("url", requireAll = false)
fun ImageView.loadUrlAsync(url: String?) {
    val anim = CircularProgressDrawable(context).apply {
        strokeWidth = 4f
        setColorSchemeColors(
            *listOf(
                R.color.main_mint, R.color.main_mint_shadow, R.color.blue_temp
            ).map { context.getColor(it) }.toIntArray()
        )
        start()
    }

    if (url == null) {
        Glide.with(this).load(anim).into(this)
    } else {
        Glide.with(this).load(url)
            .transition(withCrossFade(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()))
            .placeholder(anim).into(this)
    }
}

@BindingAdapter("srcResource")
fun ImageView.setResourceWithId(@DrawableRes id: Int) {
    setImageResource(id)
}

@BindingAdapter("android:visibility")
fun View.setVisibilityBinding(isVisible: Boolean) {
    this.isVisible = isVisible
}

@BindingAdapter("dismissKeyboardOnClick")
fun View.setOnClickListenerForDismissSoftInput(dismiss: Boolean) {
    if (dismiss) {
        setOnDebounceClickListener {
            val imm = context.getSystemService(InputMethodManager::class.java)
            imm.hideSoftInputFromWindow(rootView.windowToken, 0)
            rootView.findFocus()?.clearFocus()
        }
    }
}

@BindingAdapter("visibilityWithAnim", "duration", requireAll = false)
fun View.setVisibilityWithAnim(visible: Boolean, _duration: Long) {
    val duration = if (_duration == 0L) 200L else _duration
    if (visible) {
        animate().alpha(1f).setDuration(duration).withStartAction {
            alpha = 0f
            isVisible = true
        }.start()
    } else {
        animate().alpha(0f).setDuration(duration).withEndAction {
            isVisible = false
        }.start()
    }
}


@BindingAdapter("shadowOnScroll", "siblingDirectParentDepthDiff", requireAll = false)
fun View.setShadowOnScroll(show: Boolean, _siblingDirectParentDepthDiff: Int) {
    alpha = 0f
    if (!show) return

    val siblingDirectParentDepthDiff = if (_siblingDirectParentDepthDiff != 0) _siblingDirectParentDepthDiff else 1
    var p: View? = this

    repeat(siblingDirectParentDepthDiff) {
        p = p?.parent as? ViewGroup
        p ?: return
    }

    val recyclerView = (p as? ViewGroup)?.children?.first { it is RecyclerView } ?: return

    stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.shadow_scroll_anim)
    recyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
        isActivated = recyclerView.canScrollVertically(-1)
    }
}

@BindingAdapter("android:selected")
fun View.setSelectedBinding(isSelected: Boolean) {
    this.isSelected = isSelected
}