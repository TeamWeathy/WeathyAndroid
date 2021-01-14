package team.weathy.ui.record.detail

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.R
import team.weathy.api.CreateClothesReq
import team.weathy.api.CreateWeathyReq
import team.weathy.api.USER_ID_PATH_SEGMENT
import team.weathy.api.WeathyAPI
import team.weathy.databinding.FragmentRecordDetailBinding
import team.weathy.di.Api
import team.weathy.model.entity.ClothCategory
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.*
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.launchCatch
import team.weathy.util.extensions.showToast
import javax.inject.Inject

@AndroidEntryPoint
class RecordDetailFragment : Fragment() {
    @Inject
    @Api
    lateinit var weathyAPI: WeathyAPI
    private var binding by AutoClearedValue<FragmentRecordDetailBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordDetailBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.etDetail.addTextChangedListener(textWatcher)

        binding.layoutDetail setOnDebounceClickListener {
            hideKeyboard()
            binding.etDetail.clearFocus()
        }

        binding.etDetail.setOnFocusChangeListener { _, hasFocus ->
            setCountVisibility(hasFocus)
        }

        configureStartNavigation()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(string: Editable?) {
            val length = string.toString().length
            binding.tvTextLength2.text = "$length"

            if (length > 0) {
                if (!binding.btnConfirm.isEnabled)
                    setButtonEnabled(true)
                setTextActivation(getColor(R.color.main_mint), R.drawable.edit_border_active)
            } else {
                if (binding.btnConfirm.isEnabled)
                    setButtonDisabled(false)
                setTextActivation(getColor(R.color.sub_grey_6), R.drawable.edit_border)
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.etDetail.windowToken, 0)
    }

    private fun configureStartNavigation() {
        binding.close setOnDebounceClickListener {
            requireActivity().finish()
        }
        binding.btnConfirm setOnDebounceClickListener {
            onSubmit()
            requireContext().showToast("웨디에 내용이 추가되었어요!")
        }
    }

    private fun setCountVisibility(hasFocus: Boolean) {
        binding.tvTextLength.isVisible = hasFocus
        binding.tvTextLength2.isVisible = hasFocus
    }

    private fun setButtonEnabled(isEnable: Boolean) {
        val colorChangeActive = AnimatorInflater.loadAnimator(context, R.animator.color_change_active_anim) as AnimatorSet
        colorChangeActive.setTarget(binding.btnConfirm)
        colorChangeActive.start()
        binding.btnConfirm.isEnabled = isEnable
    }

    private fun setButtonDisabled(isEnable: Boolean) {
        val colorChange = AnimatorInflater.loadAnimator(context, R.animator.color_change_anim) as AnimatorSet
        colorChange.setTarget(binding.btnConfirm)
        colorChange.start()
        binding.btnConfirm.isEnabled = isEnable
    }

    private fun setTextActivation(color: Int, drawable: Int) {
        binding.tvTextLength2.setTextColor(color)
        binding.etDetail.setBackgroundResource(drawable)
    }

    @FlowPreview
    private fun onSubmit() {
        val userId = UniqueIdentifier.
        val date = viewModel.date.dateString
        val code =
        val clothes =
        val stampId = viewModel.selectedWeatherRatingIndex.value!! + 1
        val feedback = binding.etDetail.text.toString()

        launchCatch({
            weathyAPI.createWeathy(CreateWeathyReq(userId, date, code, clothes, stampId, feedback))
        }, onSuccess = {
            it.message
        })
    }
}