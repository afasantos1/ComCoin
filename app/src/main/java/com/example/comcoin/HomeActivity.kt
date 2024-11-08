package com.example.comcoin
import android.os.Bundle
import android.widget.*
import android.content.Intent
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<Coins>
    private lateinit var newCoinList: ArrayList<Coins>
    lateinit var name: Array<String>
    lateinit var com : Array<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_menu)

        val dbHandler = DBHandler (this)
        val newCoin = findViewById<Button>(R.id.newCoin)
        val coinsBtn = findViewById<Button>(R.id.showCoinsBtn)
        val scanBtn2 = findViewById<Button>(R.id.scanBtn2)
        val scanBtnShadow = findViewById<Button>(R.id.scanBtn3)


        val btnAnimation = AnimationUtils.loadAnimation(this, R.anim.cam_button_click_anim)


        //--------------------------------IMPLEMENTAR BASE DE DADOS--------------------------------------
        newCoinList = dbHandler.readCoins()
        //----------------------------------------------------------------------------------------------


        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf()
        getUserData()

        coinsBtn.setOnClickListener {
            val proximaPagina = Intent(this, AllCoins::class.java)
            startActivity(proximaPagina)
        }

        newCoin.setOnClickListener {
            Log.d("Teste", "AQUI")
            val proximaPagina = Intent(this, AddCoinActivity::class.java)
            startActivity(proximaPagina)
        }

        scanBtn2.setOnClickListener {
            val proximaPagina = Intent(this, ScanActivity::class.java)
            scanBtn2.startAnimation(btnAnimation)
            scanBtnShadow.startAnimation(btnAnimation)
            Log.d("???", "AAA")
            startActivity(proximaPagina)
        }
    }

    override fun onResume() {
        super.onResume()
        setContentView(R.layout.start_menu)
        val dbHandler = DBHandler (this)
        val newCoin = findViewById<Button>(R.id.newCoin)
        val coinsBtn = findViewById<Button>(R.id.showCoinsBtn)

        val scanBtn2 = findViewById<Button>(R.id.scanBtn2)
        val scanBtnShadow = findViewById<Button>(R.id.scanBtn3)

        val btnAnimation = AnimationUtils.loadAnimation(this, R.anim.cam_button_click_anim)

        newCoinList = dbHandler.readCoins()

        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf()
        getUserData()

        coinsBtn.setOnClickListener {
            val proximaPagina = Intent(this, AllCoins::class.java)
            startActivity(proximaPagina)
            finish()
        }

        newCoin.setOnClickListener {
            val proximaPagina = Intent(this, AddCoinActivity::class.java)
            startActivity(proximaPagina)
            finish()
        }

        scanBtn2.setOnClickListener {
            val proximaPagina = Intent(this, ScanActivity::class.java)
            scanBtn2.startAnimation(btnAnimation)
            scanBtnShadow.startAnimation(btnAnimation)
            Log.d("???", "AAA")
            startActivity(proximaPagina)
        }
    }

    private fun getUserData() {
        for (i in newCoinList) {
            val coin = Coins(i.Name, i.ImageURI , i.Description, i.Comemorativa)
            newArrayList.add(coin)
        }
        val adapter = RecAdapter(this, newArrayList)
        newRecyclerView.adapter = adapter


    }

}