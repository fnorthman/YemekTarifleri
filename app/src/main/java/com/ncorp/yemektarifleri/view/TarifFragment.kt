package com.ncorp.yemektarifleri.view


import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.room.Room
import com.ncorp.yemektarifleri.databinding.TarifFragmentBinding
import com.ncorp.yemektarifleri.managers.PermissionManager
import com.ncorp.yemektarifleri.managers.PermissionManager.selectedImage
import com.ncorp.yemektarifleri.model.Tarif
import com.ncorp.yemektarifleri.roomdb.TarifDB
import com.ncorp.yemektarifleri.roomdb.TarifDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class TarifFragment : Fragment() {
	private var _binding: TarifFragmentBinding? = null
	private val binding get() = _binding!!
	private lateinit var db: TarifDB
	private lateinit var tarifDao: TarifDao
	private val mDisposable = CompositeDisposable()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// PermissionManager kaydı yapılır, seçilen bitmap alınır
		PermissionManager.register(this) { bitmap: Bitmap ->
			binding.imageView.setImageBitmap(bitmap)
		}
		db=Room.databaseBuilder(requireContext(),TarifDB::class.java,"Tarifler").build()
		tarifDao=db.tarifDao()
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
		var isim = binding.nameText.text.toString()
		val malzemeler = binding.invText.text.toString()
		if (selectedImage != null) {
			val kucukBitmap = kucukBitmapOlustur(selectedImage, 300)
			val outputStream = ByteArrayOutputStream()
			kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
			val byteArray = outputStream.toByteArray()
			val tarif = Tarif(isim, malzemeler, byteArray)


			//Threading//RxJava
			mDisposable.add(tarifDao.ınsert(tarif)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::handleResponseForInsert))

		}


	}
	private fun handleResponseForInsert() {
		//Önceki fragment'a dön
		val action = TarifFragmentDirections.actionTarifFragmentToListFragment()
		Navigation.findNavController(requireView()).navigate(action)

	}

	fun delete(view: View) {
		// Tarif silme işlemi yapılır
	}



	private fun kucukBitmapOlustur(kucultulmekIstenenBitmap: Bitmap?, maximumBoyut: Int): Bitmap {
		var width = kucultulmekIstenenBitmap!!.width
		var height = kucultulmekIstenenBitmap.height

		val bitmapOrani: Double = width.toDouble() / height.toDouble()

		if (bitmapOrani > 1) {
			//gorsel yatay
			width = maximumBoyut
			val kisaltilmisYukseklik = width / bitmapOrani
			height = kisaltilmisYukseklik.toInt()
		} else {
			//gorsel dikey
			height = maximumBoyut
			val kisaltilmisGenislik = height * bitmapOrani
			width = kisaltilmisGenislik.toInt()

		}
		return Bitmap.createScaledBitmap(kucultulmekIstenenBitmap!!, width, height, true)

	}
	override fun onDestroy() {
		super.onDestroy()
		_binding = null
		mDisposable.clear()
	}
}




