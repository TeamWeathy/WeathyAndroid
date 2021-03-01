package team.weathy.ui.license

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LicenseDecoration (private val divHeigt : Int): RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = divHeigt
    }
}