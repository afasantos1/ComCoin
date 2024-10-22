package com.example.comcoin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddCoinActivity : AppCompatActivity() {
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
        val imgResourceAtual = defaultImg
        var originalName = ""
        backBtn.setOnClickListener{
            val proximaPagina = Intent(this, HomeActivity()::class.java)
            startActivity(proximaPagina)
        }

        if(intent.getBooleanExtra("edit", false)){
            originalName = intent.getStringExtra("NomeEdit").toString()
            newCoinName.setText(intent.getStringExtra("NomeEdit"))
            coinPic.setImageResource(intent.getIntExtra("ImageEdit", defaultImg))
            addInfo.setText(intent.getStringExtra("DescriptionEdit"))
            isCom.isActivated = intent.getBooleanExtra("CommemorativeEdit", false)
            edit = true
        }

            doneBtn.setOnClickListener{

                if(!edit){
            val novoNome = newCoinName.text.toString()
            val novaDesc = addInfo.text.toString()
            val novaImagem = imgResourceAtual
            val novaIsCom = isCom.isChecked
            if (novoNome.isEmpty() || novaDesc.isEmpty()) {
                Toast.makeText(this, "Please enter all the data..", Toast.LENGTH_SHORT).show()
                }

            //Função que adicione a moeda à base de dados
            dbHandler.addNewCoin(novoNome, novaDesc, novaImagem, novaIsCom)
                Toast.makeText(this, "Coin created!", Toast.LENGTH_SHORT).show()
                val proximaPagina = Intent(this, HomeActivity()::class.java)
                startActivity(proximaPagina)

            } else {
                    dbHandler.updateCoin(originalName, newCoinName.text.toString(), addInfo.text.toString(), intent.getIntExtra("ImageEdit", defaultImg), isCom.isChecked)
                    Toast.makeText(this, "Coin edited!", Toast.LENGTH_SHORT).show()
                    val proximaPagina = Intent(this, HomeActivity()::class.java)
                    startActivity(proximaPagina)
            }

        }

        coinPic.setOnClickListener{
            val proximaPagina = Intent(this, CameraActivity()::class.java)
            startActivity(proximaPagina)
        }
    }
}