package com.example.comcoin

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.provider.MediaStore
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera)

        captureBtn = findViewById(R.id.captureBtn)
        previewView = findViewById(R.id.captureImageView)


         nomeCoin = intent.getStringExtra("NomeCoin").toString()
         addInfo = intent.getStringExtra("DescricaoCoin").toString()
         isCom = intent.getBooleanExtra("isCom", false)

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
        // Create file to save the image
        val photoFile = File(filesDir, "camera_photo.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Capture image
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    // Convert the saved image file to a Bitmap
                    bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    bitmap = rotateBitmap90Degrees(bitmap)

                    // Now that the bitmap is ready, pass it to the next activity
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    saveBitmapImage(bitmap)

                    // Create Intent to pass the Bitmap to the next Activity
                    val proximaPag = Intent(this@CameraActivity, AddCoinActivity::class.java)
                    proximaPag.putExtra("URI", uriNovo.toString())
                    proximaPag.putExtra("PhotoTaken", true)
                    proximaPag.putExtra("coinName", nomeCoin)
                    proximaPag.putExtra("coinDescription", addInfo)
                    proximaPag.putExtra("isCom", isCom)
                    startActivity(proximaPag)

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

    private fun saveBitmapImage(bitmap: Bitmap) {
        val timestamp = System.currentTimeMillis()

        //Tell the media scanner about the new file so that it is immediately available to the user.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name))
        values.put(MediaStore.Images.Media.IS_PENDING, true)
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            uriNovo = uri.toString().let {
                URI.create(it)
            }!!
            Log.d("URI AQUI", uriNovo.toString())
            try {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    try {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.close()
                    } catch (e: Exception) {
                        Log.e("SAVE_BITMAP", "saveBitmapImage: ", e)
                    }
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                contentResolver.update(uri, values, null, null)

                Toast.makeText(this, "Saved...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("SAVE_BITMAP", "saveBitmapImage: ", e)
            }
        }
    }

    private fun rotateBitmap90Degrees(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f) // Rotate the bitmap 90 degrees

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

}
