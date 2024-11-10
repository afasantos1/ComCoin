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

    // Este controlador está associado ao menu inicial, em que temos uma lista das moedas do utilizador

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

        // Animacao para que, ao clicar no botão, haja um feedback visual. Neste caso, redução de tamanho e retorno ao tamanho original
        val btnAnimation = AnimationUtils.loadAnimation(this, R.anim.cam_button_click_anim)


        //--------------------------------IMPLEMENTAR BASE DE DADOS--------------------------------------

        newCoinList = dbHandler.readCoins() // Obtenção das moedas armazenadas na base de dados local

        //----------------------------------------------------------------------------------------------


        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf() // É definido este ArrayList para podermos adicionar e remover moedas da lista sem dificuldade dado o seu tamanho dinâmico

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

    // Função que transfere os dados do array newCoinList para o ArrayList, também como passar os dados para o adapter do RecyclerView
    private fun getUserData() {
        for (i in newCoinList) {
            val coin = Coins(i.Name, i.ImageURI , i.Description, i.Comemorativa)
            newArrayList.add(coin)
        }
        // Associar o adapter que criámos em RecAdapter.kt à nossa ReciclerView
        val adapter = RecAdapter(this, newArrayList)
        newRecyclerView.adapter = adapter
    }
}