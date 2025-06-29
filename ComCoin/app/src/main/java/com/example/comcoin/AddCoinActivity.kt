package com.example.comcoin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class AddCoinActivity : AppCompatActivity() {

    // Controlador associado à view responsável por criar/editar moedas

    private var uriNovo = Uri.parse(R.drawable.camera_drawable.toString())!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHandler = DBHandler (this)
        setContentView(R.layout.add_coin)

        var edit = false
        val backBtn = findViewById<Button>(R.id.backBtn)
        val doneBtn = findViewById<Button>(R.id.doneBtn)
        val coinPic = findViewById<ImageView>(R.id.coinPic)
        val newCoinName = findViewById<EditText>(R.id.newCoinName)
        val addInfo = findViewById<EditText>(R.id.addInfo)
        val isCom = findViewById<CheckBox>(R.id.comSwitch)
        val defaultImg = R.drawable.camera_drawable
        coinPic.setImageResource(defaultImg)
        var jaPicture = false

        var originalName = ""
        var originalImg = ""
        backBtn.setOnClickListener{
            val proximaPagina = Intent(this, HomeActivity()::class.java)
            startActivity(proximaPagina)
            finish()
        }



        if(intent.getBooleanExtra("edit", false) and !intent.getBooleanExtra("updateEdit", false)) // edit é a variável que define se esta view foi chamada com o intuito de editar ou nao
        {                                                                                                                            // updateEdit é a variável que define se, edit sendo true, foi tirada uma foto nova ou não
            originalName = intent.getStringExtra("NomeEdit").toString() // armazenamos o nome original e a imagem original de forma a podermos usar os seus valores mais tarde para alterar na base de dados
            originalImg = intent.getStringExtra("ImageEdit").toString()
            newCoinName.setText(intent.getStringExtra("NomeEdit"))                              //| Valores passados quando a view é chamada com intuito de editar. Estes são os valores que aparecerão já pré-preenchidos
            uriNovo = Uri.parse(intent.getStringExtra("ImageEdit"))                             //|
            coinPic.setImageURI(uriNovo)                                                              //|
            Log.d("URI do drawable", uriNovo.toString())                                          //|
            addInfo.setText(intent.getStringExtra("DescriptionEdit"))                           //|
            isCom.isChecked = intent.getBooleanExtra("CommemorativeEdit", false)     //|
            edit = true
        }

        if (intent.getBooleanExtra("PhotoTaken", false)) // "PhotoTaken" define se já foi tirada uma foto ou nao. A diferença entre esta variável e updateEdit é que updateEdit apenas é utilizada em contexto de edição
        {
            jaPicture = true // Mesmo que PhotoTaken, mas fora do intent
            newCoinName.setText(intent.getStringExtra("coinName").toString())
            addInfo.setText(intent.getStringExtra("coinDescription").toString())
            isCom.isChecked = intent.getBooleanExtra("isCom", false)
            uriNovo = Uri.parse(intent.getStringExtra("URI"))
            coinPic.setImageURI(uriNovo)
            Log.d("URI NOVO NA IMAGEM", uriNovo.toString())
            if(intent.getBooleanExtra("updateEdit", false))
            {
                originalName = intent.getStringExtra("nomeOriginal").toString()  // Recuperação dos valores originais de nome e imagem após trocarmos de view no cotexto de edição
                originalImg = intent.getStringExtra("imgOriginal").toString()
            }
        }

        doneBtn.setOnClickListener{
            val editUpdate = intent.getBooleanExtra("updateEdit", false)
            if(!edit and !editUpdate) // adicionar moeda nova
            {
                val novoNome = newCoinName.text.toString()
                val novaDesc = addInfo.text.toString()
                val novaImagem = uriNovo
                val novaIsCom = isCom.isChecked
                if (novoNome.isEmpty() || novaDesc.isEmpty()) // caso não haja nome ou descricao
                {
                    Toast.makeText(this, "Please enter all the data", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    if(!jaPicture) // caso ainda não tenha sido tirada a foto da moeda
                    {
                        Toast.makeText(this, "Update coin picture", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        //Função que adiciona a moeda à base de dados
                        dbHandler.addNewCoin(novoNome, novaDesc, novaImagem.toString(), novaIsCom)
                        Log.d("URI novo", uriNovo.toString())
                        Toast.makeText(this, "Coin created!", Toast.LENGTH_SHORT).show()
                        val proximaPagina = Intent(this, HomeActivity()::class.java)
                        startActivity(proximaPagina)
                        finish()
                    }
                }
            }
            else // editar moeda
            {
                dbHandler.updateCoin(originalName, originalImg, newCoinName.text.toString(), addInfo.text.toString(), uriNovo.toString(), isCom.isChecked) // Função que altera a moeda na base de dados local
                Log.d("URI NOVO", uriNovo.toString())
                if(editUpdate)
                {
                    deletePhoto(Uri.parse(originalImg)) // Apagar foto no armazenamento local
                }
                Toast.makeText(this, "Coin edited!", Toast.LENGTH_SHORT).show()
                val proximaPagina = Intent(this, HomeActivity()::class.java)
                startActivity(proximaPagina)
                finish()
            }
        }

        coinPic.setOnClickListener{
            val proximaPagina = Intent(this, CameraActivity()::class.java)
            proximaPagina.putExtra("edit", edit)
            proximaPagina.putExtra("NomeCoin", newCoinName.text.toString())
            proximaPagina.putExtra("DescricaoCoin", addInfo.text.toString())
            proximaPagina.putExtra("isCom", isCom.isChecked)
            proximaPagina.putExtra("nomeOriginal", originalName)
            proximaPagina.putExtra("imgOriginal", originalImg)
            startActivity(proximaPagina)
        }
    }

    fun deletePhoto(uri: Uri) {
        // Transformar o uri em path
        val file = File(uri.path ?: return)

        // Verificação da existência do ficheiro
        if (file.exists()) {
            val deleted = file.delete()
            if (deleted) {
                Log.d("PhotoDelete", "Photo deleted successfully: ${file.name}")
            } else {
                Log.e("PhotoDelete", "Failed to delete photo: ${file.name}")
            }
        } else {
            Log.e("PhotoDelete", "File does not exist: ${file.path}")
        }
    }


}
