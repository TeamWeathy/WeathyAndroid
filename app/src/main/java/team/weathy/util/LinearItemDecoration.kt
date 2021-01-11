package team.weathy.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearItemDecoration(private val dp: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = dp.dp
    }
}