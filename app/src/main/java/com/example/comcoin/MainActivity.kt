package com.example.comcoin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storage = FirebaseStorage.getInstance()


        //get component
        val pagina = findViewById<ConstraintLayout>(R.id.main)
        val myText = findViewById<View>(R.id.pressTextView) as TextView

        val anim: Animation = AlphaAnimation(0.7f, 1.0f)
        anim.duration = 400 //You can manage the blinking time with this parameter
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        myText.startAnimation(anim)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        pagina.setOnClickListener{
            val proximaPagina = Intent(this, HomeActivity::class.java)
            startActivity(proximaPagina)
            finish()

        }
    }
}