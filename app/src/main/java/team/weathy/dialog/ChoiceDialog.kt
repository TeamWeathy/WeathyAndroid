package team.weathy.dialog

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
import team.weathy.databinding.DialogChoiceBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.PixelRatio
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class ChoiceDialog : DialogFragment() {
    private var binding by AutoClearedValue<DialogChoiceBinding>()

    @Inject
    lateinit var pixelRatio: PixelRatio

    private val title: String
        get() = arguments?.getString("title") ?: ""
    private val choice1: String
        get() = arguments?.getString("choice1") ?: ""
    private val choice2: String
        get() = arguments?.getString("choice2") ?: ""
    private val clickListener: ClickListener?
        get() = if (parentFragment == null) (activity as? ClickListener) else (parentFragment as? ClickListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DialogChoiceBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false
        binding.title.text = title
        binding.choice1.text = choice1
        binding.choice2.text = choice2
        binding.choice1 setOnDebounceClickListener {
            clickListener?.onClickChoice1()
            dismiss()
        }
        binding.choice2 setOnDebounceClickListener {
            clickListener?.onClickChoice2()
            dismiss()
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
        fun onClickChoice1() {}
        fun onClickChoice2() {}
    }

    companion object {
        fun newInstance(
            title: String? = null,
            choice1: String? = null,
            choice2: String? = null
        ) = ChoiceDialog().apply {
            arguments = bundleOf(
                "title" to title, "choice1" to choice1, "choice2" to choice2
            )
        }
    }
}