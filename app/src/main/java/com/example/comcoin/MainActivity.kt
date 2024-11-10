package com.example.comcoin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {

    // Ecrã inicial, apenas com o logo e com uma indicação para clicar em qualquer lugar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val storage = FirebaseStorage.getInstance() // Ainda não é utilizado, mas futuramente será


        val pagina = findViewById<ConstraintLayout>(R.id.main)
        val myText = findViewById<View>(R.id.pressTextView) as TextView

        val anim: Animation = AlphaAnimation(0.7f, 1.0f) // Animação para o texto piscar
        anim.duration = 400
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        myText.startAnimation(anim)


        pagina.setOnClickListener{
            val proximaPagina = Intent(this, HomeActivity::class.java)
            startActivity(proximaPagina)
            finish()
        }
    }
}