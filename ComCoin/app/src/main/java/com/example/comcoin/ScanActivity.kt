package com.example.comcoin

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Size
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var module: Module
    private val executor = Executors.newSingleThreadExecutor()
    private val REQUEST_CAMERA_PERMISSION = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        previewView = findViewById(R.id.captureImageView)
        val captureBtn = findViewById<Button>(R.id.captureBtn)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            startCamera()
        }

        module = Module.load(assetFilePath("model.pt"))

        captureBtn.setOnClickListener {
            takePictureAndPredict()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePictureAndPredict() {
        val bitmap = previewView.bitmap ?: return
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            resized,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )

        val output = module.forward(IValue.from(inputTensor)).toTensor()
        val scores = output.dataAsFloatArray

        // Apply softmax to get probabilities
        val expScores = scores.map { Math.exp(it.toDouble()) }
        val sumExp = expScores.sum()
        val probabilities = expScores.map { it / sumExp }

        val maxIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val confidence = probabilities[maxIdx] * 100.0

        val result = if (confidence >= 75.0) {
            var pais = ""
            var ano =  0

            if (maxIdx == 81){
                pais = "Grécia"
                ano = 2004
            }
            if (maxIdx == 105){
                pais = "França"
                ano = 2007
            }
            if(maxIdx == 22){
                pais = "Países Baixos"
                ano = 2013
            }
            if(maxIdx == 225){
                pais = "Portugal"
                ano = 2016
            }
            if(maxIdx == 372){
                pais = "Portugal"
                ano = 2016
            }

            "Classe da moeda reconhecida: $maxIdx\nPaís de origem: $pais\n Ano de lançamento: $ano\nConfiança: ${"%.2f".format(confidence)}%"
        } else {
            "Não foi possível reconhecer a moeda com confiança.\n(Confiança: ${"%.2f".format(confidence)}%)"
        }

        showResult(result)
    }


    private fun showResult(result: String) {
        AlertDialog.Builder(this)
            .setTitle("Prediction")
            .setMessage(result)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun assetFilePath(assetName: String): String {
        val file = File(filesDir, assetName)
        if (file.exists() && file.length() > 0) return file.absolutePath

        assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }
        return file.absolutePath
    }
}
