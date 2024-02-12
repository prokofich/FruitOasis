package com.example.orchardoasis.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.orchardoasis.R
import com.example.orchardoasis.databinding.FragmentRulesBinding
import com.example.orchardoasis.model.constant.GAME

class RulesFragment : Fragment() {

    private var binding: FragmentRulesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRulesBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // переход обратно в меню
        binding?.idRulesButtonBack?.setOnClickListener {
            GAME.navController?.navigate(R.id.action_rulesFragment_to_menuFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}