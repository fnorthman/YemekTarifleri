package com.ncorp.yemektarifleri


import android.graphics.Bitmap

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import com.ncorp.yemektarifleri.databinding.TarifFragmentBinding


class TarifFragment : Fragment() {
	private var _binding: TarifFragmentBinding? = null
	private val binding get() = _binding!!

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// PermissionManager kaydı yapılır, seçilen bitmap alınır
		PermissionManager.register(this) { bitmap: Bitmap ->
			binding.imageView.setImageBitmap(bitmap)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = TarifFragmentBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Görsel seçmek için imageView'a tıklanır
		binding.imageView.setOnClickListener {
			PermissionManager.checkGalleryPermission(view)
		}

		binding.saveButton.setOnClickListener { save(it) }
		binding.deleteButton.setOnClickListener { delete(it) }

		arguments?.let {
			val bilgi = TarifFragmentArgs.fromBundle(it).bilgi
			if (bilgi == "yeni") {
				binding.saveButton.isEnabled = true
				binding.deleteButton.isEnabled = false
				binding.nameText.setText("")
				binding.invText.setText("")
			} else {
				binding.saveButton.isEnabled = false
				binding.deleteButton.isEnabled = true
			}
		}
	}

	fun save(view: View) {
		// Tarif kaydetme işlemi yapılır
	}

	fun delete(view: View) {
		// Tarif silme işlemi yapılır
	}

	override fun onDestroy() {
		super.onDestroy()
		_binding = null
	}
}