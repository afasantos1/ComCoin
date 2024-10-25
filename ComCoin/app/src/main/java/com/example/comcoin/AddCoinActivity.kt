package com.example.comcoin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddCoinActivity : AppCompatActivity() {
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

        var originalName = ""
        backBtn.setOnClickListener{
            val proximaPagina = Intent(this, HomeActivity()::class.java)
            startActivity(proximaPagina)
        }



        if(intent.getBooleanExtra("edit", false)){
            originalName = intent.getStringExtra("NomeEdit").toString()
            newCoinName.setText(intent.getStringExtra("NomeEdit"))
            uriNovo = Uri.parse(intent.getStringExtra("ImageEdit"))
            coinPic.setImageURI(uriNovo)
        Log.d("URI do drawable", uriNovo.toString())
            addInfo.setText(intent.getStringExtra("DescriptionEdit"))
            isCom.isActivated = intent.getBooleanExtra("CommemorativeEdit", false)
            edit = true
        }

        if (intent.getBooleanExtra("PhotoTaken", false)) {
            newCoinName.setText(intent.getStringExtra("coinName").toString())
            addInfo.setText(intent.getStringExtra("coinDescription").toString())
            isCom.isChecked = intent.getBooleanExtra("isCom", false)
            uriNovo = Uri.parse(intent.getStringExtra("URI"))
            Log.d("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", uriNovo.toString())
            coinPic.setImageURI(uriNovo)
            Log.d("URI NOVO NA IMAGEM", uriNovo.toString())
        }

            doneBtn.setOnClickListener{

                        if(!edit){
                    val novoNome = newCoinName.text.toString()
                    val novaDesc = addInfo.text.toString()
                    val novaImagem = uriNovo
                    val novaIsCom = isCom.isChecked
                    if (novoNome.isEmpty() || novaDesc.isEmpty()) {
                        Toast.makeText(this, "Please enter all the data..", Toast.LENGTH_SHORT).show()
                        }

                    //Função que adicione a moeda à base de dados
                    dbHandler.addNewCoin(novoNome, novaDesc, novaImagem.toString(), novaIsCom)
                            Log.d("BBBBBBBBBBBBBBBBBB", novaImagem.toString())
                            Log.d("URI novo", uriNovo.toString())
                        Toast.makeText(this, "Coin created!", Toast.LENGTH_SHORT).show()
                        val proximaPagina = Intent(this, HomeActivity()::class.java)
                        startActivity(proximaPagina)

                    } else {
                            dbHandler.updateCoin(originalName, newCoinName.text.toString(), addInfo.text.toString(), uriNovo.toString(), isCom.isChecked)
                            Toast.makeText(this, "Coin edited!", Toast.LENGTH_SHORT).show()
                            val proximaPagina = Intent(this, HomeActivity()::class.java)
                            startActivity(proximaPagina)
                    }


        }

        coinPic.setOnClickListener{
            val proximaPagina = Intent(this, CameraActivity()::class.java)
            proximaPagina.putExtra("NomeCoin", newCoinName.text.toString())
            proximaPagina.putExtra("DescricaoCoin", addInfo.text.toString())
            proximaPagina.putExtra("isCom", isCom.isChecked)
            startActivity(proximaPagina)
        }
    }


    }
