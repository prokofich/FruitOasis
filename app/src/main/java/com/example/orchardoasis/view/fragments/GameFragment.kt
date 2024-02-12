package com.example.orchardoasis.view.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.orchardoasis.R
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.orchardoasis.databinding.FragmentGameBinding
import com.example.orchardoasis.model.adapter.GameAdapter
import com.example.orchardoasis.model.adapter.InterfaceAdapter
import com.example.orchardoasis.model.constant.CHERRY
import com.example.orchardoasis.model.constant.COMPLEXITY
import com.example.orchardoasis.model.constant.DIAMOND
import com.example.orchardoasis.model.constant.GAME
import com.example.orchardoasis.model.constant.LEMON
import com.example.orchardoasis.model.constant.WIN
import com.example.orchardoasis.model.constant.listAllFruit
import com.example.orchardoasis.model.constant.listFruitsForGame
import com.example.orchardoasis.model.constant.listQuestionsForGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment(),InterfaceAdapter {

    private var countSc:Int = 0
    private var countCorrectAnswers = 0
    private var binding: FragmentGameBinding? = null
    private var mainFruit = ""
    private var job:Job = Job()

    private var recyclerView: RecyclerView? = null
    private var gameAdapter: GameAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(inflater,container,false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        countSc = requireArguments().getInt(COMPLEXITY) // 6,5 или 4 секунды на запоминание
        mainFruit = listAllFruit.shuffled()[1] // выбранный случайный фрукт
        setCountCorrectAnswers()

        // привязка к recyclerView
        recyclerView = binding?.idGameRv
        gameAdapter = GameAdapter(requireContext(),this)
        recyclerView?.layoutManager = GridLayoutManager(requireContext(),4)
        recyclerView?.adapter = gameAdapter
        gameAdapter?.setListQuestion(listQuestionsForGame) // закрытие показа фруктов

        // начать игру
        binding?.idGameButtonGo?.setOnClickListener {
            job = CoroutineScope(Dispatchers.Main).launch {
                startGame()
                showMainFruit()
            }
        }

        // закончить игру и выйти в меню
        binding?.idGameButtonFinish?.setOnClickListener {
            if(job.isActive){
                job.cancel()
            }
            GAME.navController?.navigate(R.id.action_gameFragment_to_menuFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    @SuppressLint("SetTextI18n")
    private suspend fun startGame(){
        binding?.idGameTvTimer?.isVisible = true
        binding?.idGameButtonGo?.isVisible = false
        binding?.idGameButtonFinish?.isVisible = true
        gameAdapter?.setList(listFruitsForGame.shuffled())
        for (i in countSc downTo 0){
            binding?.idGameTvTimer?.text = "$i seconds"
            delay(1000)
        }
        gameAdapter?.setListQuestion(listQuestionsForGame)
        binding?.idGameTvTimer?.isVisible = false
        binding?.idGameCsMainFruit?.isVisible = true
        gameAdapter?.setMainFruit(mainFruit)
        gameAdapter?.setCountMainFruit(countCorrectAnswers)
        gameAdapter?.setBlock(false)
    }

    private fun showMainFruit(){
        when(mainFruit){
            LEMON   -> { binding?.idGameIvMainFruit?.load(R.drawable.f) }
            CHERRY  -> { binding?.idGameIvMainFruit?.load(R.drawable.g) }
            DIAMOND -> { binding?.idGameIvMainFruit?.load(R.drawable.s) }
        }
    }

    private fun setCountCorrectAnswers(){
        when(mainFruit){
            LEMON   -> { countCorrectAnswers = 5 }
            CHERRY  -> { countCorrectAnswers = 5 }
            DIAMOND -> { countCorrectAnswers = 6 }
        }
    }

    override fun finish(result: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("GAME OVER")

        if(result == WIN){
            builder.setMessage("Congratulations! You've won!")
        }else{
            builder.setMessage("unfortunately you lost")
        }

        builder.setPositiveButton("start again") { dialog, which ->
            GAME.navController?.navigate(R.id.action_gameFragment_to_complexityLevelFragment)
        }

        builder.setNegativeButton("go to menu") { dialog, which ->
            GAME.navController?.navigate(R.id.action_gameFragment_to_menuFragment)
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}