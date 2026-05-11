package com.sciencequiz.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.sciencequiz.app.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var photoPath: String? = null

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnCapture.setOnClickListener {
            capturePhoto()
        }

        binding.btnConfirm.setOnClickListener {
            confirmPhoto()
        }

        binding.btnRetake.setOnClickListener {
            retakePhoto()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.previewView.surfaceProvider)

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                binding.overlayCamera.visibility = android.view.View.GONE
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to start camera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        val imageCapture = this.imageCapture ?: return

        val photoDir = File(filesDir, "photos")
        if (!photoDir.exists()) photoDir.mkdirs()

        val photoFile = File(
            photoDir,
            "profile_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    photoPath = photoFile.absolutePath
                    binding.btnCapture.visibility = android.view.View.GONE
                    binding.btnConfirm.visibility = android.view.View.VISIBLE
                    binding.btnRetake.visibility = android.view.View.VISIBLE
                    binding.overlayCamera.visibility = android.view.View.VISIBLE
                    binding.overlayCamera.setImageURI(android.net.Uri.fromFile(photoFile))
                    Toast.makeText(this@CameraActivity, "Photo captured!", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun confirmPhoto() {
        val path = photoPath
        if (path != null) {
            val prefs = getSharedPreferences("sciencequest_prefs", MODE_PRIVATE)
            prefs.edit().putString("profile_photo_path", path).apply()
            Toast.makeText(this, "Profile photo saved!", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun retakePhoto() {
        photoPath = null
        binding.btnCapture.visibility = android.view.View.VISIBLE
        binding.btnConfirm.visibility = android.view.View.GONE
        binding.btnRetake.visibility = android.view.View.GONE
        binding.overlayCamera.visibility = android.view.View.GONE
    }
}
