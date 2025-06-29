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

    // Este controlador está associado a uma página que contém, listadas, todas as moedas não comemorativas

    lateinit var images : List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_coins)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val assetManager = this.assets // Usado para ir buscar a pasta com as imagens aos assets
        val directoryPath = "Default" // Nome da pasta
        images = assetManager.list(directoryPath)?.filter { it.endsWith(".png") }!! // Dos ficheiros da pasta, apenas seleciona aqueles que têm a extensão .png


        val leftColumnLayout = findViewById<LinearLayout>(R.id.leftColumnLayout)
        val rightColumnLayout = findViewById<LinearLayout>(R.id.rightColumnLayout)
        val backBtn = findViewById<Button>(R.id.backBtn)
        val searchBox = findViewById<EditText>(R.id.searchBox)

        // Usamos um TextWatcher para que o que o utilizador inserir na searchBox seja imediatamente usado enquanto é alterado, não necessitando de um botão, por exemplo
        searchBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                leftColumnLayout.removeAllViews()
                rightColumnLayout.removeAllViews() // No momento em que é alterado o texto da searchBox, ambas as colunas são limpas para os seus conteúdos serem filtrados
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

    // Função usada para carregar especificamente imagens dos assets, transformando-as em bitmaps para poderem ser utilizadas mais tarde
    fun loadImageFromAssets(fileName: String, imageView: ImageView) {
        val assetManager = this.assets
        val inputStream = assetManager.open("Default/$fileName")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        imageView.setImageBitmap(bitmap)
        inputStream.close()
    }

    // Função auxiliar apenas usada para retirar o sufixo .png dos nomes das imagens
    fun cutString(main: String, toRemove: String): String {
        return main.replace(toRemove, "").removeSuffix(".png")
    }

    // Insere as imagens e texto relativo a cada imagem na view, dividindo-as por duas colunas
    fun createViews(images : List<String>, leftColumnLayout : LinearLayout, rightColumnLayout : LinearLayout){
        images.forEachIndexed { index, fileName ->


            // Cria uma ImageView para cada imagem
            val imageView = ImageView(this).apply {
                // Definição de tamanho para todas as ImageViews adicionadas terem as mesmas dimensões
                layoutParams = LinearLayout.LayoutParams(
                    (200 * context.resources.displayMetrics.density).toInt(),
                    (200 * context.resources.displayMetrics.density).toInt()

                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }.apply {

                // Função definida acima. Usamos para associar a imagem à ImageView
                loadImageFromAssets(fileName, this)
            }

            val textView = TextView(this).apply {
                text = cutString(fileName, "Default") // Como os nomes de todas as imagens seguem o formato "PaísDefault.png" ou "PaísDefault{numero}.png", cortamos o nome de forma a dar "País" ou "País{numero}"
                setPadding(0, 0, 0, 16)
                gravity = Gravity.CENTER
            }


            // Divisão das imagens e texto pelas colunas
            if (index % 2 == 0) {
                leftColumnLayout.addView(imageView)
                leftColumnLayout.addView(textView)
            } else {
                rightColumnLayout.addView(imageView)
                rightColumnLayout.addView(textView)
            }
        }
    }

    // Filtragem das imagens
    fun findByName(nome: String, lista: List<String>): ArrayList<String> {
        val novaLista = ArrayList<String>()
        val nomeLower = nome.lowercase() // Convertemos o valor inserido para minusculas

        for (i in lista) {
            if (i.lowercase().startsWith(nomeLower)) { // Ao comparar, metemos tudo em minusculas para a nossa filtragem nao ser case-sensitive
                novaLista.add(i)
            }
        }
        return novaLista
    }

}