package com.example.ocrreconizer

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.ocrreconizer.databinding.FragmentFirstBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.lang.Exception
import java.util.jar.Manifest

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var binding: FragmentFirstBinding? = null
    private var recognizer: TextRecognizer? = null
    private var bitmap: Bitmap? = null
    private var step = 0
    private var itens = arrayListOf<String>()

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        setupTextRecognizer()
        setupClickListener()
        setTextForDescription()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_CODE
            )
        }
    }

    private fun setupTextRecognizer() {
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    private fun setupClickListener() {
        binding?.buttonCapture?.setOnClickListener {
            context?.let { ctx ->
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(ctx, this)
            }
        }

        binding?.buttonBuild?.setOnClickListener {
           findNavController().navigate(FirstFragmentDirections.actionFirstFragmentToSecondFragment(itens.toTypedArray()))
        }
    }

    private fun getTextFromImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer?.process(image)?.addOnSuccessListener { text ->
            handleText(text.textBlocks)
        }
            ?.addOnFailureListener {
            }
    }

    private fun handleText(textBlocks: List<Text.TextBlock>) {
        for (block in textBlocks) {
            for (line in block.lines) {
                itens.add(line.text)
            }
        }
    }


    private fun setTextForDescription() {
        binding?.tvStep?.text =
            "Primeiramente, tire uma foto do estabelecimento e depois de um it em por vez"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val uri = result.uri
                try {
                    step.inc()
                    bitmap =
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                    bitmap?.let {
                        getTextFromImage(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        const val REQUEST_CAMERA_CODE = 100
    }
}