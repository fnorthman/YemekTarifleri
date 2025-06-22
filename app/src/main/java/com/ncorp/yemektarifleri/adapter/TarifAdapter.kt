package com.ncorp.yemektarifleri.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ncorp.yemektarifleri.databinding.RecyclerRowBinding
import com.ncorp.yemektarifleri.model.Tarif
import com.ncorp.yemektarifleri.view.ListFragmentDirections

class TarifAdapter(var tarifListesi: List<Tarif>): RecyclerView.Adapter<TarifAdapter.TarifHolder>() {
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): TarifHolder {
		val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
		return TarifHolder(recyclerRowBinding)
	}

	override fun onBindViewHolder(
		holder: TarifHolder,
		position: Int
	) {
		holder.binding.recyclerRowTextView.text = tarifListesi[position].isim

		holder.binding.root.setOnClickListener {
			val action = ListFragmentDirections.actionListFragmentToTarifFragment("eski", tarifListesi[position].id)
			Navigation.findNavController(it).navigate(action)
		}


	}

	override fun getItemCount(): Int {
		return tarifListesi.size
	}

	class TarifHolder(val binding: RecyclerRowBinding): RecyclerView.ViewHolder(binding.root){

	}
}