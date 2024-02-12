package com.example.orchardoasis.model.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.orchardoasis.R
import com.example.orchardoasis.model.constant.CHERRY
import com.example.orchardoasis.model.constant.DIAMOND
import com.example.orchardoasis.model.constant.LEMON
import com.example.orchardoasis.model.constant.LOSS
import com.example.orchardoasis.model.constant.QUESTION
import com.example.orchardoasis.model.constant.WIN

class GameAdapter(private val context: Context, private val interfaceGame:InterfaceAdapter): RecyclerView.Adapter<GameAdapter.FruitsViewHolder>() {

    private var listFruits = emptyList<String>()
    private var listFruits2 = emptyList<String>()
    private var mainFruit = ""
    private var countMainFruit = 0
    private var countCorrectAnswers = 0
    private var blockFruits = true

    class FruitsViewHolder(view: View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FruitsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv,parent,false)
        return FruitsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listFruits.size
    }

    override fun onBindViewHolder(holder: FruitsViewHolder, position: Int) {
        val imageView = holder.itemView.findViewById<ImageView>(R.id.id_item_rv_iv)
        when(listFruits[position]){
            QUESTION -> { imageView.load(R.drawable.a) }
            LEMON    -> { imageView.load(R.drawable.f) }
            CHERRY   -> { imageView.load(R.drawable.g) }
            DIAMOND  -> { imageView.load(R.drawable.s) }
        }
    }

    override fun onViewAttachedToWindow(holder: FruitsViewHolder) {
        super.onViewAttachedToWindow(holder)

        val imageView = holder.itemView.findViewById<ImageView>(R.id.id_item_rv_iv)

        holder.itemView.setOnClickListener {
            if(!blockFruits){
                if(listFruits[holder.adapterPosition] == QUESTION){
                    when(listFruits2[holder.adapterPosition]){
                        LEMON   -> { imageView.load(R.drawable.f) }
                        CHERRY  -> { imageView.load(R.drawable.g) }
                        DIAMOND -> { imageView.load(R.drawable.s) }
                    }
                    if(listFruits2[holder.adapterPosition]==mainFruit){
                        Toast.makeText(context,"correct!",Toast.LENGTH_SHORT).show()
                        countCorrectAnswers += 1
                        if(countCorrectAnswers == countMainFruit){
                            interfaceGame.finish(WIN) // победа
                        }
                    }else{
                        interfaceGame.finish(LOSS) // поражение
                    }
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(lst:List<String>){
        listFruits = lst
        listFruits2 = lst
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setListQuestion(lst:List<String>){
        listFruits = lst
        notifyDataSetChanged()
    }

    // установка выбранного фрукта
    fun setMainFruit(frt:String){
        mainFruit = frt
    }

    // закрытие/открытие возможности нажатия на элементы
    fun setBlock(flag:Boolean){
        blockFruits = flag
    }

    // установка количества правильных элементов
    fun setCountMainFruit(cnt:Int){
        countMainFruit = cnt
    }

}