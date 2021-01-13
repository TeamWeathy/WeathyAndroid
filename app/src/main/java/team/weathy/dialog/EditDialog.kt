package team.weathy.dialog

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
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
import dagger.hilt.android.AndroidEntryPoint
import team.weathy.R
import team.weathy.databinding.DialogEditBinding
import team.weathy.util.AutoClearedValue
import team.weathy.util.PixelRatio
import team.weathy.util.extensions.getColor
import team.weathy.util.setOnDebounceClickListener
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class EditDialog : DialogFragment() {
	private var binding by AutoClearedValue<DialogEditBinding>()

	@Inject
	lateinit var pixelRatio: PixelRatio

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
			binding.textDeleteBtn.isVisible = !it.isNullOrBlank()
			binding.textDeleteBtn setOnDebounceClickListener {
				binding.enter.setText("")
			}
			if (!it.isNullOrBlank()) {
				if (!binding.btnAdd.isEnabled)
					setButtonEnabled(true)
				binding.textCount.setTextColor(color)
				binding.textCount.text = it.length.toString()
				binding.enter.setBackgroundResource(R.drawable.edit_border_active)
			} else {
				if (binding.btnAdd.isEnabled)
					setButtonDisabled(false)
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

	private fun setButtonEnabled(isEnable: Boolean) {
		val colorChangeActive = AnimatorInflater.loadAnimator(context, R.animator.color_change_active_anim) as AnimatorSet
		colorChangeActive.setTarget(binding.btnAdd)
		colorChangeActive.start()
		binding.btnAdd.isEnabled = isEnable
	}

	private fun setButtonDisabled(isEnable: Boolean) {
		val colorChange = AnimatorInflater.loadAnimator(context, R.animator.color_change_anim) as AnimatorSet
		colorChange.setTarget(binding.btnAdd)
		colorChange.start()
		binding.btnAdd.isEnabled = isEnable
	}
}