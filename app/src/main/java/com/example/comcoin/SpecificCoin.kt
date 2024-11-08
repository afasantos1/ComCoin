package com.example.comcoin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

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
        val imgCoin = intent.getStringExtra("coinImage")

        coinPic.setImageURI(Uri.parse(imgCoin))
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
            dbHandler.deleteCoin(intent.getStringExtra("coinImage"))
            deletePhoto(Uri.parse(imgCoin))
            Toast.makeText(this, "Coin Eliminated", Toast.LENGTH_SHORT).show()
            val proximaPag = Intent(this, HomeActivity::class.java)
            startActivity(proximaPag)
            finish()
        }

        editBtn.setOnClickListener {
            val proximaPag = Intent(this, AddCoinActivity::class.java)
            proximaPag.putExtra("edit", true)
            proximaPag.putExtra("NomeEdit", coinName.text)
            proximaPag.putExtra("DescriptionEdit", coinDesc.text)
            proximaPag.putExtra("ImageEdit", imgCoin.toString())
            Log.d("imgCoin Specific Coin", imgCoin.toString())
            proximaPag.putExtra("CommemorativeEdit", intent.getBooleanExtra("Comemorativa", false))
            startActivity(proximaPag)
            finish()
        }

    }

    fun deletePhoto(uri: Uri) {
        // Convert the URI to a File object
        val file = File(uri.path ?: return) // Safely handle null path

        // Check if the file exists before trying to delete it
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