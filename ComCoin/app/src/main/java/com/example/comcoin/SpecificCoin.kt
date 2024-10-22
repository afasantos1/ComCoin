package com.example.comcoin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SpecificCoin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.specific_coin)

        val backBtn = findViewById<Button>(R.id.backBtn)
        val editBtn = findViewById<Button>(R.id.editBtn)
        val elimBtn = findViewById<Button>(R.id.elimCoinBtn)
        val coinPic = findViewById<ImageView>(R.id.coinPic)
        val coinName = findViewById<TextView>(R.id.coinName)
        val coinDesc = findViewById<TextView>(R.id.coinDesc)
        val star = findViewById<ImageView>(R.id.commemorativeStar)
        val imgCoin = intent.getIntExtra("coinImage", 0)

        coinPic.setImageResource(imgCoin)
        coinName.text = intent.getStringExtra("coinName")
        coinDesc.text = intent.getStringExtra("coinDescription")

        if (intent.getBooleanExtra("Comemorativa", false))
        {
            star.setImageResource(android.R.drawable.btn_star_big_on)
        }

        backBtn.setOnClickListener {
            val proximaPag = Intent(this, HomeActivity::class.java)
            startActivity(proximaPag)
        }

        elimBtn.setOnClickListener {
            val dbHandler = DBHandler (this)
            dbHandler.deleteCoin(intent.getStringExtra("coinName"))
            Toast.makeText(this, "Coin Eliminated", Toast.LENGTH_SHORT).show()
            val proximaPag = Intent(this, HomeActivity::class.java)
            startActivity(proximaPag)
        }

        editBtn.setOnClickListener {
            val proximaPag = Intent(this, AddCoinActivity::class.java)
            proximaPag.putExtra("edit", true)
            proximaPag.putExtra("NomeEdit", coinName.text)
            proximaPag.putExtra("DescriptionEdit", coinDesc.text)
            proximaPag.putExtra("ImageEdit", imgCoin)
            proximaPag.putExtra("CommemorativeEdit", intent.getBooleanExtra("Comemorativa", false))
            startActivity(proximaPag)
        }

    }
}