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

        val backBtn = findViewById<Button>(R.id.backBtn)
        val doneBtn = findViewById<Button>(R.id.doneBtn)
        val coinPic = findViewById<ImageView>(R.id.coinPic)
        val newCoinName = findViewById<EditText>(R.id.newCoinName)
        val addInfo = findViewById<EditText>(R.id.addInfo)
        val isCom = findViewById<CheckBox>(R.id.comSwitch)
        val defaultImg = R.drawable.camera_drawable
        coinPic.setImageResource(defaultImg)
        val imgResourceAtual = defaultImg
        backBtn.setOnClickListener{
            val proximaPagina = Intent(this, HomeActivity()::class.java)
            startActivity(proximaPagina)
        }

        doneBtn.setOnClickListener{
            val novoNome = newCoinName.text.toString()
            val novaDesc = addInfo.text.toString()
            val novaImagem = imgResourceAtual
            val novaIsCom = isCom.isActivated
            if (novoNome.isEmpty() || novaDesc.isEmpty()) {
                Toast.makeText(this, "Please enter all the data..", Toast.LENGTH_SHORT).show()
            }
            else{
            //Função que adicione a moeda à base de dados
            dbHandler.addNewCoin(novoNome, novaDesc, novaImagem, novaIsCom)
                Toast.makeText(this, "Moeda criada com sucesso!!", Toast.LENGTH_SHORT).show()
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