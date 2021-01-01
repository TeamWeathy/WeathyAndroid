package team.weathy.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("srcUrl", "placeholder", requireAll = false)
fun ImageView.loadUrlAsync(url: String?, placeholder: Drawable? = null) {
    if (url == null) {
        Glide.with(this).load(placeholder).into(this)
    } else {
        Glide.with(this).load(url).apply {
            if (placeholder != null) placeholder(placeholder)
        }.into(this)
    }
}

@BindingAdapter("android:visibility")
fun View.setVisibilityBinding(isVisible: Boolean) {
    this.isVisible = isVisible
}
