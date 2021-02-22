package team.weathy.ui.record.detail

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.*
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import team.weathy.R
import team.weathy.databinding.FragmentRecordDetailBinding
import team.weathy.dialog.ChoiceDialog
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.enableWithAnim
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@FlowPreview
@AndroidEntryPoint
class RecordDetailFragment : Fragment(), ChoiceDialog.ClickListener {
    private var binding by AutoClearedValue<FragmentRecordDetailBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    private val REQUEST_IMAGE_CAPTURE = 1 // 사진 촬영 요청코드
    lateinit var curPhotoPath: String // 문자열 형태의 사진 경로 값

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
            if (!viewModel.edit) binding.btnConfirm.enableWithAnim(it!!)
        }
    }

    private fun submit(includeFeedback: Boolean) = lifecycleScope.launchWhenCreated {
        viewModel.submit(includeFeedback)
    }

    private fun configureButton() {
        if (viewModel.edit) {
            binding.btnConfirm.text = "수정완료"
            binding.btnConfirm.setBackgroundColor(getColor(R.color.main_mint))
            binding.btnConfirm.isEnabled = true
        } else {
            binding.btnConfirm.text = "내용 추가하기"
        }
    }

    private fun configureImage() {
        binding.addImage setOnDebounceClickListener {
            showChoiceDialog()
        }
    }

    private fun showChoiceDialog() {
        ChoiceDialog.newInstance(
            "사진 추가하기",
            "앨범에서 사진 선택",
            "카메라 촬영").show(childFragmentManager, null)
    }

    override fun onClickChoice1() {
        lifecycleScope.launchWhenStarted {
            checkPermission()
        }

    }

    override fun onClickChoice2() {
        lifecycleScope.launchWhenStarted {
            setPermission()
            takePicture() // 사진 촬영
        }
    }

    private fun setPermission() {
        val permission = object : com.gun0912.tedpermission.PermissionListener {
            override fun onPermissionGranted() {
                Log.d("테스트", "권한이 허용됨")
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Log.d("테스트", "권한이 거부됨")
            }
        }

        TedPermission.with(requireContext())
            .setPermissionListener(permission)
            .setRationaleMessage("카메라 앱을 사용하시려면 권한을 허용해주세요.")
            .setDeniedMessage("권한을 거부하셨습니다. 설정에서 허용해주세요.")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .check()
    }

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(), "team.weathy.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
            .apply { curPhotoPath = absolutePath }
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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val bitmap: Bitmap
            val file = File(curPhotoPath)

            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.fromFile(file))
                binding.photo.setImageBitmap(bitmap)
            } else {
                val decode = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file))
                bitmap = ImageDecoder.decodeBitmap(decode)
                binding.photo.setImageBitmap(bitmap)
            }
            savePhoto(bitmap)
        }

        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            fileUri = data?.data!!
            Glide.with(this).load(fileUri).into(binding.photo)
            setDeleteImageButton()
        }
    }

    private fun savePhoto(bitmap: Bitmap) {
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/"
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timestamp}.jpeg"
        val folder = File(folderPath)
        if (!folder.isDirectory) {
            folder.mkdirs()
        }

        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(JPEG, 100, out)
        Log.d("테스트", "사진 저장 완료")
    }

    private fun setDeleteImageButton() {
        binding.addImage.isClickable = false
        binding.deleteImg.isEnabled = true
        binding.deleteImg.isVisible = true
        binding.deleteImg setOnDebounceClickListener {
            binding.photo.setImageDrawable(null)
            binding.addImage.isClickable = true
            binding.deleteImg.isEnabled = false
            binding.deleteImg.isVisible = false
        }
    }
}