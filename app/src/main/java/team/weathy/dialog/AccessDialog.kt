package team.weathy.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.R
import team.weathy.databinding.DialogAccessBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.PixelRatio
import team.weathy.util.extensions.getColor
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class AccessDialog : DialogFragment() {
    private var binding by AutoClearedValue<DialogAccessBinding>()

    @Inject
    lateinit var pixelRatio: PixelRatio

    private val mainTitle: String
        get() = arguments?.getString("mainTitle") ?: ""
    private val subTitle1: String
        get() = arguments?.getString("subTitle1") ?: ""
    private val subTitle2: String
        get() = arguments?.getString("subTitle2") ?: ""
    private val body1: String
        get() = arguments?.getString("body1") ?: ""
    private val body2: String
        get() = arguments?.getString("body2") ?: ""
    private val detail: String
        get() = arguments?.getString("detail") ?: ""
    private val btnText: String
        get() = arguments?.getString("btnText") ?: ""
    private val color: Int
        get() = arguments?.getInt("color", getColor(R.color.blue_temp))
                ?: getColor(R.color.blue_temp)
    private val clickListener: ClickListener?
        get() = if (parentFragment == null) (activity as? ClickListener) else (parentFragment as? ClickListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            DialogAccessBinding.inflate(inflater, container, false).also {
                binding = it
            }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = true
        binding.title.text = mainTitle
        binding.positionTitle.text = subTitle1
        binding.cameraTitle.text = subTitle2
        binding.positionBody.text = body1
        binding.cameraBody.text = body2
        binding.detail.text = detail
        binding.btn.text = btnText
        binding.btn setOnDebounceClickListener {
            clickListener?.navigateMainWithPermissionCheck()
            dismiss()
        }
        binding.title.setTextColor(color)
        binding.btn.backgroundTintList = ColorStateList.valueOf(color)
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
        fun navigateMainWithPermissionCheck(){}
    }

    companion object {
        fun newInstance(
                mainTitle: String? = null,
                subTitle1: String? = null,
                subTitle2: String? = null,
                body1: String? = null,
                body2: String? = null,
                detail: String? = null,
                btnText: String? = null,
                color: Int? = null,
        ) = AccessDialog().apply {
            arguments = bundleOf(
                    "mainTitle" to mainTitle, "subTitle1" to subTitle1, "subTitle2" to subTitle2, "body1" to body1, "body2" to body2, "detail" to detail, "btnText" to btnText, "color" to color
            )
        }
    }
}