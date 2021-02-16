package team.weathy.ui.record.detail

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.R
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

    private val PICK_FROM_ALBUM = 100
    private var fileUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordDetailBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        configureTextField()
        configureSubmitBehaviors()
        configureButton()
        configureImage()
    }

    private fun configureTextField() {
        binding.layoutDetail setOnDebounceClickListener {
            hideKeyboard()
            binding.skip.isVisible = true
            binding.div.isVisible = true
        }

        binding.etDetail.setOnFocusChangeListener { _, hasFocus ->
            viewModel.feedbackFocused.value = hasFocus
            binding.skip.isVisible = false
            binding.div.isVisible = false
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(binding.etDetail.windowToken, 0)
        binding.etDetail.clearFocus()
    }

    private fun configureSubmitBehaviors() {
        binding.skip setOnDebounceClickListener {
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

    private fun configureButton() {
        if (viewModel.edit) {
            binding.btnConfirm.text = "수정완료"
        } else {
            binding.btnConfirm.text = "내용 추가하기"
        }
    }

    private fun configureImage() {
        binding.addImage setOnDebounceClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {
        var temp = ""

        if(ContextCompat.checkSelfPermission
            (requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " "
        }

        if(ContextCompat.checkSelfPermission
            (requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " "
        }

        if(!TextUtils.isEmpty(temp)) {
            ActivityCompat.requestPermissions(requireActivity(), temp.trim().split(" ").toTypedArray(), 1)
        } else {
            val intent = Intent(Intent.ACTION_PICK)

            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, PICK_FROM_ALBUM)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1) {
            val length = permissions.size

            for(i in 0 until length) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

                    intent.type = MediaStore.Images.Media.CONTENT_TYPE
                    intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    startActivityForResult(intent, PICK_FROM_ALBUM)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_FROM_ALBUM) {
            if(resultCode == RESULT_OK) {
                fileUri = data?.data!!
                Glide.with(this).load(fileUri).into(binding.imgRecord)
                setDeleteImageButton()
            }
        }
    }

    private fun setDeleteImageButton() {
        binding.addImage.isClickable = false
        binding.deleteImg.isEnabled = true
        binding.deleteImg.isVisible = true
        binding.deleteImg setOnDebounceClickListener {
            binding.imgRecord.setImageResource(R.drawable.logo512)
            binding.addImage.isClickable = true
            binding.deleteImg.isEnabled = false
            binding.deleteImg.isVisible = false
        }
    }
}