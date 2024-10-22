package com.example.comcoin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

//    private lateinit var captureIV: ImageView
    private lateinit var captureBtn: Button
    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var drawable: Drawable
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera)

        captureBtn = findViewById(R.id.captureBtn)
        previewView = findViewById(R.id.captureImageView)

        // Initialize the camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }

        captureBtn.setOnClickListener {
            takePhoto()
            val proximaPag = Intent(this, AddCoinActivity::class.java)
            proximaPag.putExtra("ImagemTirada", bitmap)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview UseCase
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // ImageCapture UseCase
            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Create file to save the image
        val photoFile = File(filesDir, "camera_photo.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Capture image
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Handle the saved image Uri
                    val savedUri = Uri.fromFile(photoFile)

                    // Convert the saved image file to a Bitmap
                    bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                    // Convert the Bitmap to a Drawable
                    drawable = BitmapDrawable(resources, bitmap)

                    // Now, you can set the image to an ImageView or store the drawable for later use
//                    captureIV.setImageDrawable(drawable)


                    Log.d("PhotoCapture", "Image saved and converted to Drawable")
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }
            }
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        // Shutdown the camera executor
        cameraExecutor.shutdown()
    }
}
