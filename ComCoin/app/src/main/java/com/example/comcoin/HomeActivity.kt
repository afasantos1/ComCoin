package com.example.comcoin
import android.os.Bundle
import android.widget.*
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<Coins>
    private lateinit var newCoinList: ArrayList<Coins>
    lateinit var imageID: Array<Int>
    lateinit var name: Array<String>
    lateinit var desc: Array<String>
    lateinit var com : Array<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_menu)

        val dbHandler = DBHandler (this)
        val newCoin = findViewById<Button>(R.id.newCoin)
        val backBtn = findViewById<Button>(R.id.backBtn)



        //--------------------------------IMPLEMENTAR BASE DE DADOS--------------------------------------
        newCoinList = dbHandler.readCoins()
        //----------------------------------------------------------------------------------------------


        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf()
        getUserData()

        backBtn.setOnClickListener {
            val proximaPagina = Intent(this, MainActivity::class.java)
            startActivity(proximaPagina)

        }

        newCoin.setOnClickListener {
            Log.d("Teste", "AQUI")
            val proximaPagina = Intent(this, AddCoinActivity::class.java)
            startActivity(proximaPagina)
        }
    }

    override fun onResume() {
        super.onResume()
        setContentView(R.layout.start_menu)
        val dbHandler = DBHandler (this)
        val newCoin = findViewById<Button>(R.id.newCoin)
        val backBtn = findViewById<Button>(R.id.backBtn)

        newCoinList = dbHandler.readCoins()

        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf<Coins>()
        getUserData()

        backBtn.setOnClickListener {
            val proximaPagina = Intent(this, MainActivity::class.java)
            startActivity(proximaPagina)

        }

        newCoin.setOnClickListener {
            Log.d("Teste", "AQUI")
            val proximaPagina = Intent(this, AddCoinActivity::class.java)
            startActivity(proximaPagina)
        }
    }

    private fun getUserData() {
        for (i in newCoinList) {
            val coin = Coins(i.Name, i.Image , i.Description, i.Comemorativa)
            newArrayList.add(coin)
        }
        val adapter = RecAdapter(this, newArrayList)
        newRecyclerView.adapter = adapter


    }

}