package team.weathy.ui.record.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.databinding.FragmentRecordDetailBinding
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.enableWithAnim
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener

@FlowPreview
@AndroidEntryPoint
class RecordDetailFragment : Fragment() {
    private var binding by AutoClearedValue<FragmentRecordDetailBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordDetailBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.layoutDetail setOnDebounceClickListener {
            hideKeyboard()
        }

        binding.etDetail.setOnFocusChangeListener { _, hasFocus ->
            viewModel.feedbackFocused.value = hasFocus
        }

        configureSubmitBehaviors()
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(binding.etDetail.windowToken, 0)
        binding.etDetail.clearFocus()
    }

    private fun configureSubmitBehaviors() {
        binding.close setOnDebounceClickListener {
            submit(false)
        }
        binding.btnConfirm setOnDebounceClickListener {
            submit(true)
        }
        requireActivity().onBackPressedDispatcher.addCallback {
            submit(false)
        }

        viewModel.onRecordSuccess.observe(viewLifecycleOwner) {
            requireContext().showToast("웨디에 내용이 추가되었어요!")
            requireActivity().finish()
        }
        viewModel.onRecordEdited.observe(viewLifecycleOwner) {
            requireContext().showToast("웨디 내용이 수정되었어요!")
            requireActivity().finish()
        }
        viewModel.onRecordFailed.observe(viewLifecycleOwner) {
            requireContext().showToast("내용 추가가 실패했어요!")
        }

        viewModel.isSubmitButtonEnabled.observe(viewLifecycleOwner) {
            binding.btnConfirm.enableWithAnim(it)
        }
    }

    private fun submit(includeFeedback: Boolean) = lifecycleScope.launchWhenCreated {
        viewModel.submit(includeFeedback)
    }
}