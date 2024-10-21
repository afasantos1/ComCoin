package com.example.comcoin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SpecificCoin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.specific_coin)

        val backBtn = findViewById<Button>(R.id.backBtn)
        val editBtn = findViewById<Button>(R.id.editBtn)
        val coinPic = findViewById<ImageView>(R.id.coinPic)
        val coinName = findViewById<TextView>(R.id.coinName)
        val coinDesc = findViewById<TextView>(R.id.coinDesc)
        val star = findViewById<ImageView>(R.id.commemorativeStar)

        coinPic.setImageResource(intent.getIntExtra("coinImage", 0))
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

    }
}