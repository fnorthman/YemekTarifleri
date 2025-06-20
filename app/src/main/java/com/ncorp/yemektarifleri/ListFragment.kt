package com.ncorp.yemektarifleri

import android.graphics.Path.Direction
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ncorp.yemektarifleri.databinding.ListFragmentBinding


class ListFragment : Fragment() {

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            YeniEkle(it)


        }
    }
    fun YeniEkle (view: View){
        val action = ListFragmentDirections.actionListFragmentToTarifFragment("yeni",0)
        Navigation.findNavController(view).navigate(action)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}


