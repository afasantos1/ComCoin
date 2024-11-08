package com.example.comcoin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


class AllCoins : AppCompatActivity() {
    lateinit var images : List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_coins)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val assetManager = this.assets
        val directoryPath = "Default"
        images = assetManager.list(directoryPath)?.filter { it.endsWith(".png") }!!

        val layoutHorizontal = findViewById<LinearLayout>(R.id.layoutHorizontal)
        val leftColumnLayout = findViewById<LinearLayout>(R.id.leftColumnLayout)
        val rightColumnLayout = findViewById<LinearLayout>(R.id.rightColumnLayout)
        val backBtn = findViewById<Button>(R.id.backBtn)
        val searchBox = findViewById<EditText>(R.id.searchBox)


        searchBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                leftColumnLayout.removeAllViews()
                rightColumnLayout.removeAllViews()
                }

            override fun afterTextChanged(p0: Editable?) {
               val novoTexto = searchBox.text.toString()
               val novasImagens = findByName(novoTexto, images)
               createViews(novasImagens, leftColumnLayout, rightColumnLayout)
            }

        })


        createViews(images, leftColumnLayout, rightColumnLayout)


        backBtn.setOnClickListener {
            val proximaPagina = Intent(this, HomeActivity::class.java)
            startActivity(proximaPagina)
            finish()
        }

    }

    fun loadImageFromAssets(fileName: String, imageView: ImageView) {
        val assetManager = this.assets
        val inputStream = assetManager.open("Default/$fileName")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        imageView.setImageBitmap(bitmap)
        inputStream.close()
    }

    fun cutString(main: String, toRemove: String): String {
        return main.replace(toRemove, "").removeSuffix(".png")
    }

    fun createViews(images : List<String>, leftColumnLayout : LinearLayout, rightColumnLayout : LinearLayout){
        images?.forEachIndexed { index, fileName ->


            // Create a new ImageView for each image
            val imageView = ImageView(this).apply {
                // Set uniform layout parameters for fixed size
                layoutParams = LinearLayout.LayoutParams(
                    (200 * context.resources.displayMetrics.density).toInt(),
                    (200 * context.resources.displayMetrics.density).toInt()

                )
                scaleType = ImageView.ScaleType.CENTER_CROP // Crop to fill the fixed size
            }.apply {

                // Load the image from assets into the new ImageView
                loadImageFromAssets(fileName, this)
            }

            val textView = TextView(this).apply {
                text = cutString(fileName, "Default")
                setPadding(0, 0, 0, 16) // Adds padding to the bottom (16 pixels here as an example)
                gravity = Gravity.CENTER // Centers the text within the TextView
            }



            // Add the ImageView to the left or right column alternately
            if (index % 2 == 0) {
                leftColumnLayout.addView(imageView)
                leftColumnLayout.addView(textView)
            } else {
                rightColumnLayout.addView(imageView)
                rightColumnLayout.addView(textView)
            }
        }
    }

    fun findByName(nome: String, lista: List<String>): ArrayList<String> {
        val novaLista = ArrayList<String>()
        val nomeLower = nome.lowercase() // Convert the search term to lowercase

        for (i in lista) {
            if (i.lowercase().startsWith(nomeLower)) { // Convert each item to lowercase for comparison
                novaLista.add(i)
            }
        }
        return novaLista
    }

}