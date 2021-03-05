package team.weathy.ui.record.detail

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.*
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
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
import android.view.WindowManager
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import okhttp3.MediaType
import okhttp3.RequestBody
import team.weathy.R
import team.weathy.databinding.FragmentRecordDetailBinding
import team.weathy.dialog.ChoiceDialog
import team.weathy.ui.record.RecordViewModel
import team.weathy.util.AutoClearedValue
import team.weathy.util.extensions.enableWithAnim
import team.weathy.util.extensions.getColor
import team.weathy.util.extensions.showToast
import team.weathy.util.setOnDebounceClickListener
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

@FlowPreview
@AndroidEntryPoint
class RecordDetailFragment : Fragment(), ChoiceDialog.ClickListener {
    private var binding by AutoClearedValue<FragmentRecordDetailBinding>()
    private val viewModel by activityViewModels<RecordViewModel>()

    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var curPhotoPath: String

    private val PICK_FROM_ALBUM = 100
    private var fileUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRecordDetailBinding.inflate(layoutInflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (viewModel.imgFromEdit != null) {
            Glide.with(this).load(viewModel.imgFromEdit).into(binding.photo)
            setDeleteImageButton()
            viewModel.imgFromEdit = null
        }

        configureTextField()
        configureSubmitBehaviors()
        configureButton()
        configureImage()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback {
            submit(false)
        }
    }

    private fun configureTextField() {
        binding.layoutDetail setOnDebounceClickListener {
            hideKeyboard()
        }

        binding.etDetail.setOnFocusChangeListener { _, hasFocus ->
            setKeyboardMode()
            viewModel.feedbackFocused.value = hasFocus
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(binding.etDetail.windowToken, 0)
        binding.etDetail.clearFocus()
    }

    private fun setKeyboardMode() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun configureSubmitBehaviors() {
        binding.skip setOnDebounceClickListener {
            submit(false)
        }
        binding.btnConfirm setOnDebounceClickListener {
            submit(true)
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
            toggleSubmitButton()
        }
    }

    private fun toggleSubmitButton() {
        if (!viewModel.edit) {
            if ((viewModel.isSubmitButtonEnabled.value!! || binding.photo.drawable != null)) {
                binding.btnConfirm.enableWithAnim(true)
            } else {
                binding.btnConfirm.enableWithAnim(false)
            }
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
            binding.skip.isVisible = false
        } else {
            binding.btnConfirm.text = "내용 추가하기"
            binding.skip.isVisible = true
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
        }
    }

    private fun setPermission() {
        var temp = ""

        if(ContextCompat.checkSelfPermission
            (requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " "
        }

        if(ContextCompat.checkSelfPermission
            (requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.CAMERA + " "
        }

        if(!TextUtils.isEmpty(temp)) {
            ActivityCompat.requestPermissions(requireActivity(), temp.trim().split(" ").toTypedArray(), 1)
        } else {
            takePicture()
        }
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

        lateinit var bitmap: Bitmap

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val file = File(curPhotoPath)

            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.fromFile(file))
                binding.photo.setImageBitmap(bitmap)
            } else {
                val decode = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file))
                bitmap = ImageDecoder.decodeBitmap(decode)
                binding.photo.setImageBitmap(bitmap)
            }
            toggleSubmitButton()
        }

        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            fileUri = data?.data!!
            Glide.with(this).load(fileUri)
                .listener(object: RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        binding.photo.setImageDrawable(resource)
                        toggleSubmitButton()
                        return true
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        TODO("Not yet implemented")
                    }
                })
                .into(binding.photo)

            val options = BitmapFactory.Options()
            val inputStream: InputStream = requireActivity().contentResolver.openInputStream(fileUri!!)!!
            bitmap = BitmapFactory.decodeStream(inputStream, null, options)!!
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(JPEG, 20, byteArrayOutputStream)
        viewModel.img.value = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray())
        viewModel.isDelete.value = false

        setDeleteImageButton()
    }

    private fun setDeleteImageButton() {
        binding.addImage.isClickable = false
        binding.deleteImg.isEnabled = true
        binding.deleteImg.isVisible = true
        binding.deleteImg setOnDebounceClickListener {
            binding.photo.setImageDrawable(null)
            viewModel.img.value = null
            viewModel.isDelete.value = true
            binding.addImage.isClickable = true
            binding.deleteImg.isEnabled = false
            binding.deleteImg.isVisible = false

            toggleSubmitButton()
        }
    }
}