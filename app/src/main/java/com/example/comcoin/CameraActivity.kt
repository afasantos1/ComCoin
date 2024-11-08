package com.example.comcoin

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.provider.MediaStore
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var captureBtn: Button
    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var bitmap: Bitmap
    private lateinit var uriNovo: URI
    private lateinit var nomeCoin : String
    private lateinit var addInfo : String
    private var isCom = false
    private var edit = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera)

        captureBtn = findViewById(R.id.captureBtn)
        val animation = AnimationUtils.loadAnimation(this, R.anim.cam_button_click_anim)

        previewView = findViewById(R.id.captureImageView)


         nomeCoin = intent.getStringExtra("NomeCoin").toString()
         addInfo = intent.getStringExtra("DescricaoCoin").toString()
         isCom = intent.getBooleanExtra("isCom", false)
         edit = intent.getBooleanExtra("edit", false)

            // Initialize the camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }

        captureBtn.setOnClickListener {
            captureBtn.startAnimation(animation)
            Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show()
            takePhoto()
        }

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview UseCase
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
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
        // Create file in internal storage to save the picture taken
        val timestamp = System.currentTimeMillis() // Using timestamp, we can assure that the pictures wont get overriten
        val photoFile = File(filesDir, "camera_photo_$timestamp.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Capture image
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    // Create an Intent to pass the URI of the internal storage file to the next Activity
                    val uriNovo = Uri.fromFile(photoFile) // Create URI for the internal storage file
                    val proximaPag = Intent(this@CameraActivity, AddCoinActivity::class.java)
                    proximaPag.putExtra("URI", uriNovo.toString()) // Pass the URI
                    proximaPag.putExtra("PhotoTaken", true)
                    proximaPag.putExtra("coinName", nomeCoin)
                    proximaPag.putExtra("coinDescription", addInfo)
                    proximaPag.putExtra("isCom", isCom)
                    proximaPag.putExtra("updateEdit", edit)
                    proximaPag.putExtra("nomeOriginal", intent.getStringExtra("nomeOriginal"))
                    proximaPag.putExtra("imgOriginal", intent.getStringExtra("imgOriginal"))
                    startActivity(proximaPag)
                    finish()
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
