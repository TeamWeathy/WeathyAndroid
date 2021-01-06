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
import team.weathy.MainApplication.Companion.pixelRatio
import team.weathy.databinding.DialogEditBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.setOnDebounceClickListener
import kotlin.math.roundToInt

class EditDialog : DialogFragment() {
	private var binding by AutoClearedValue<DialogEditBinding>()

	private val title: String
		get() = arguments?.getString("title") ?: ""
	private val hint: String
		get() = arguments?.getString("hint") ?: ""
	private val count: String
		get() = arguments?.getString("count") ?: ""
	private val clickListener: ClickListener?
		get() = if (parentFragment == null) (activity as? ClickListener) else (parentFragment as? ClickListener)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
		DialogEditBinding.inflate(inflater, container, false).also { binding = it }.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		binding.title.text = title
		binding.enter.hint = hint
		binding.tagCount.text = count
		binding.btnAdd setOnDebounceClickListener {
			clickListener?.onClickYes(binding.enter.text?.toString() ?: "")

			dismiss()
		}
		binding.btnCancel setOnDebounceClickListener {
			clickListener?.onClickNo()
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
		fun onClickYes(text: String) {}
		fun onClickNo() {}
	}

	companion object {
		fun newInstance(
			title: String? = null,
			hint: String? = null,
			count: String? = null
		) = EditDialog().apply {
			arguments = bundleOf("title" to title, "hint" to hint, "count" to count)
		}
	}
}