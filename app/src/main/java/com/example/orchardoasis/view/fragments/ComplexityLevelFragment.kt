package com.example.orchardoasis.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import com.example.orchardoasis.R
import android.view.ViewGroup
import com.example.orchardoasis.databinding.FragmentComplexityBinding
import com.example.orchardoasis.model.constant.COMPLEXITY
import com.example.orchardoasis.model.constant.EASY
import com.example.orchardoasis.model.constant.GAME
import com.example.orchardoasis.model.constant.HARD
import com.example.orchardoasis.model.constant.MIDDLE

class ComplexityLevelFragment : Fragment() {

    private var binding: FragmentComplexityBinding? = null
    private var bundleForGame:Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentComplexityBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // переход обратно в меню
        binding?.idCompButtonBack?.setOnClickListener {
            GAME.navController?.navigate(R.id.action_complexityLevelFragment_to_menuFragment)
        }

        // выбор легкого уровня
        binding?.idCompButtonEasy?.setOnClickListener {
            bundleForGame = Bundle()
            bundleForGame?.putInt(COMPLEXITY, EASY)
            GAME.navController?.navigate(R.id.action_complexityLevelFragment_to_gameFragment,bundleForGame)
        }

        // выбор среднего уровня
        binding?.idCompButtonMiddle?.setOnClickListener {
            bundleForGame = Bundle()
            bundleForGame?.putInt(COMPLEXITY, MIDDLE)
            GAME.navController?.navigate(R.id.action_complexityLevelFragment_to_gameFragment,bundleForGame)
        }

        // выбор сложного уровня
        binding?.idCompButtonHard?.setOnClickListener {
            bundleForGame = Bundle()
            bundleForGame?.putInt(COMPLEXITY, HARD)
            GAME.navController?.navigate(R.id.action_complexityLevelFragment_to_gameFragment,bundleForGame)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}