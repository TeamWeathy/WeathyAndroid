package team.weathy.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import team.weathy.MainApplication.Companion.pixelRatio
import team.weathy.R
import team.weathy.databinding.DialogEditBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.getColor
import team.weathy.util.setOnDebounceClickListener
import kotlin.math.roundToInt

class EditDialog : DialogFragment() {
	private var binding by AutoClearedValue<DialogEditBinding>()

	private val title: String
		get() = arguments?.getString("title") ?: ""
	private val count: String
		get() = arguments?.getString("count") ?: ""
	private val color: Int
		get() = arguments?.getInt("color", getColor(R.color.blue_temp)) ?: getColor(R.color.blue_temp)
	private val clickListener: ClickListener?
		get() = if (parentFragment == null) (activity as? ClickListener) else (parentFragment as? ClickListener)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
		DialogEditBinding.inflate(inflater, container, false).also { binding = it }.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		binding.title.text = title
		binding.tagCount.text = count
		binding.btnAdd setOnDebounceClickListener {
			binding.enter.text?.toString()?.let { it1 -> clickListener?.onClickYes(it1) }

			dismiss()
		}
		binding.btnCancel setOnDebounceClickListener {
			clickListener?.onClickNo()

			dismiss()
		}
		binding.enter.setOnFocusChangeListener { _, hasFocus ->
			setCountVisibility(hasFocus)
		}
		binding.enter.addTextChangedListener {
			binding.btnAdd.isEnabled = !it.isNullOrBlank()
			binding.textDeleteBtn.isVisible = !it.isNullOrBlank()
			if (!it.isNullOrBlank()) {
				binding.btnAdd.setBackgroundColor(color)
				binding.textCount.setTextColor(color)
				binding.textCount.text = it.length.toString()
				binding.enter.setBackgroundResource(R.drawable.edit_border_active)
			} else {
				binding.btnAdd.setBackgroundColor(getColor(R.color.sub_grey_3))
				binding.textCount.setTextColor(getColor(R.color.sub_grey_6))
				binding.textCount.text = "0"
				binding.enter.setBackgroundResource(R.drawable.edit_border)
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
		fun onClickYes(text: String) {}
		fun onClickNo() {}
	}

	companion object {
		fun newInstance(
			title: String? = null,
			count: String? = null,
			color: Int? = null
		) = EditDialog().apply {
			arguments = bundleOf("title" to title, "count" to count, "color" to color)
		}
	}

	private fun setCountVisibility(hasFocus: Boolean) {
		binding.textCount.isVisible = hasFocus
		binding.textCount2.isVisible = hasFocus
	}
}