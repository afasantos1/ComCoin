package com.example.comcoin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView


// Classe usada para definir dinamicamente as views na pagina principal. Recebe um arrayList de moedas e cria views para cada moeda
class RecAdapter(private val context: Context, private val coinsList: ArrayList<Coins>) : RecyclerView.Adapter<RecAdapter.MyViewHolder>() {

    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return coinsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = coinsList[position] // Selecionar moeda a utilizar
        holder.coinImage.setImageURI(Uri.parse(currentItem.ImageURI)) // Definir imagem na ImageView
        holder.coinName.text = currentItem.Name // Definir nome na TextView
    }

    inner class MyViewHolder(itemView: View, listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        val coinImage: ShapeableImageView = itemView.findViewById(R.id.coin_image)
        val coinName: TextView = itemView.findViewById(R.id.coin_name)

        init {
            itemView.setOnClickListener { // definir que a nova itemView adicionada é clicável
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedCoin = coinsList[position]
                    val intent = Intent(context, SpecificCoin::class.java).apply {
                        putExtra("coinImage", selectedCoin.ImageURI)
                        putExtra("coinName", selectedCoin.Name)
                        putExtra("coinDescription", selectedCoin.Description)
                        putExtra("Comemorativa", selectedCoin.Comemorativa)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
}
