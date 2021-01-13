package team.weathy.view

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import team.weathy.R


class RecordFab @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatImageButton(context, attrs) {
    init {
        isEnabled = true
        isClickable = true
        isFocusable = true

        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.mtrl_btn_state_list_anim)
        setImageResource(R.drawable.ic_plus_shadow)
    }
}