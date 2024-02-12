package com.example.orchardoasis.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.orchardoasis.R
import com.example.orchardoasis.databinding.FragmentMenuBinding
import com.example.orchardoasis.model.constant.GAME

class MenuFragment : Fragment() {

    private var binding:FragmentMenuBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // выход из игры
        binding?.idMenuButtonExit?.setOnClickListener {
            GAME.finishAffinity()
        }

        // переход к правилам игры
        binding?.idMenuButtonRules?.setOnClickListener {
            GAME.navController?.navigate(R.id.action_menuFragment_to_rulesFragment)
        }

        // переход к выбору сложности игры
        binding?.idMenuButtonPlay?.setOnClickListener {
            GAME.navController?.navigate(R.id.action_menuFragment_to_complexityLevelFragment)
        }

    }

    // функция очистки биндинга
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // функция скрытия контента
    fun hideContent() {
        binding?.idMenuButtonExit?.isVisible = false
        binding?.idMenuButtonPlay?.isVisible = false
        binding?.idMenuTvTitle?.isVisible = false
        binding?.idMenuButtonRules?.isVisible = false
    }
}