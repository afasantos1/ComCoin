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

    // Controlador associado à view encarregue de tirar foto de moeda

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

            // Inicializar camera
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Tratamento de permissões
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
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Projeta imagem da camara na previewView
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            // Prepara a captura de imagem
            imageCapture = ImageCapture.Builder().build()

            // Define a camera traseira como default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Dá unibind em tudo antes de dar rebind
                cameraProvider.unbindAll()

                // Dá bind em todos os useCases na Camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Cria um ficheiro dentro do armazenamento interno para guardar a foto
        val timestamp = System.currentTimeMillis() // Ao usar um time stamp, podemos garantir que todas as fotos sao guardadas com paths diferentes e não há overrites
        val photoFile = File(filesDir, "comCoin_$timestamp.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Captura de imagem
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    // A partir daqui, usaremos o URI com o path para acessar cada imagem quando necessário
                    val uriNovo = Uri.fromFile(photoFile) // Obtenção do URI da imagem guardada
                    val proximaPag = Intent(this@CameraActivity, AddCoinActivity::class.java)
                    proximaPag.putExtra("URI", uriNovo.toString())
                    proximaPag.putExtra("PhotoTaken", true)
                    proximaPag.putExtra("coinName", nomeCoin)
                    proximaPag.putExtra("coinDescription", addInfo)
                    proximaPag.putExtra("isCom", isCom)
                    proximaPag.putExtra("updateEdit", edit)
                    proximaPag.putExtra("nomeOriginal", intent.getStringExtra("nomeOriginal")) // Caso a camera seja inicializada com intuito de edição, temos de voltar a passar os dados originais para podermos utilizá-los na função de edição
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
        // Forçar o executor da camera a desligar
        cameraExecutor.shutdown()
    }

}
