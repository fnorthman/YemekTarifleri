package com.ncorp.yemektarifleri

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ncorp.yemektarifleri.databinding.TarifFragmentBinding


class TarifFragment : Fragment() {

    private var _binding: TarifFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = TarifFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnClickListener { image(it) }
        binding.saveButton.setOnClickListener { save(it) }
        binding.deleteButton.setOnClickListener { delete(it) }

        arguments?.let {
            val bilgi = TarifFragmentArgs.fromBundle(it).bilgi
            if (bilgi.equals("yeni"))
            {//Yeni Tarif eklenecek
                binding.saveButton.isEnabled = true
                binding.deleteButton.isEnabled = false
                binding.nameText.setText(null)
                binding.invText.setText(null)

            } else {//Eski Tarig Gösteriliyor
                binding.saveButton.isEnabled = false
                binding.deleteButton.isEnabled = true
            }

        }
    }
    fun save (view: View){}
    fun delete (view: View){}
    fun image (view: View){}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
