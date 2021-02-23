package team.weathy.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.R
import team.weathy.databinding.DialogCommonBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.PixelRatio
import team.weathy.util.dp
import team.weathy.util.extensions.getColor
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class CommonDialog : DialogFragment() {
    private var binding by AutoClearedValue<DialogCommonBinding>()

    @Inject
    lateinit var pixelRatio: PixelRatio

    private val title: String
        get() = arguments?.getString("title") ?: ""
    private val body: String
        get() = arguments?.getString("body") ?: ""
    private val btnText: String
        get() = arguments?.getString("btnText") ?: ""
    private val btnCancelText: String
        get() = arguments?.getString("btnCancelText") ?: ""
    private val color: Int
        get() = arguments?.getInt("color", getColor(R.color.blue_temp)) ?: getColor(R.color.blue_temp)
    private val showCancel: Boolean
        get() = arguments?.getBoolean("showCancel") ?: false
    private val clickListener: ClickListener?
        get() = if (parentFragment == null) (activity as? ClickListener) else (parentFragment as? ClickListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DialogCommonBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false
        binding.title.text = title
        binding.body.text = body
        binding.btn.text = btnText
        binding.btn setOnDebounceClickListener {
            clickListener?.onClickYes()
            dismiss()
        }
        binding.title.setTextColor(color)
        binding.btn.backgroundTintList = ColorStateList.valueOf(color)

        if (showCancel) {
            binding.btn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                leftMargin = 13.dp
            }
            binding.btnCancel.isVisible = true
            binding.btnCancel.text = btnCancelText
            binding.btnCancel setOnDebounceClickListener {
                clickListener?.onClickNo()
                dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val width = (pixelRatio.screenShort * 0.88f).coerceAtMost(pixelRatio.toPixel(309).toFloat())
        dialog?.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.2f)
            setLayout(width.roundToInt(), WRAP_CONTENT)
        }
    }

    interface ClickListener {
        fun onClickYes() {}
        fun onClickNo() {}
    }

    companion object {
        fun newInstance(
            title: String? = null,
            body: String? = null,
            btnText: String? = null,
            btnCancelText: String? = null,
            color: Int? = null,
            showCancel: Boolean = false
        ) = CommonDialog().apply {
            arguments = bundleOf(
                "title" to title, "body" to body, "btnText" to btnText, "btnCancelText" to btnCancelText,"color" to color, "showCancel" to showCancel
            )
        }
    }
}