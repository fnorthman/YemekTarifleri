package com.ncorp.yemektarifleri.managers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

// Bu sınıf (object), galeriye erişim izni istemek ve galeriye gidip görsel seçmek için kullanılır.
// Tek bir yerden izin ve galeri işlemlerini yönetebilmek için object olarak tanımlanmıştır.
object PermissionManager {

	// İzin istemek için kullanılan ActivityResultLauncher
	private lateinit var permissionLauncher: ActivityResultLauncher<String>

	// Galeriyi açmak için kullanılan ActivityResultLauncher
	private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

	// Kullanıcının seçtiği görseli dışarı aktarmak için callback (geri çağırma fonksiyonu)
	public var onImageSelected: ((Bitmap) -> Unit)? = null
	public var selectedImage: Bitmap? = null

	// Bu object hangi fragment'te çalışıyorsa onu burada tutarız (gereken yerlerde erişmek için)
	private var fragment: Fragment? = null

	// Bu fonksiyon fragment içinde çağrılarak launch işlemleri burada tanımlanır
	fun register(fragment: Fragment, onImageSelectedCallback: (Bitmap) -> Unit) {
		// Fragment referansını saklıyoruz
		this.fragment = fragment

		// Dışarıdan gelen bitmap işlemini burada saklıyoruz (callback)
		this.onImageSelected = onImageSelectedCallback

		// İzin isteme işlemi tanımlanıyor
		permissionLauncher =
			fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
				// Kullanıcı izni verdi mi?
				if (granted) {
					// Evet verdiyse, galeri açılır
					openGallery()
				} else {
					// Hayır verdiyse, kullanıcıya bilgi verilir
					Toast.makeText(fragment.requireContext(), "İzin reddedildi", Toast.LENGTH_LONG).show()
				}
			}

		// Galeriden resim seçme işlemi tanımlanıyor
		galleryLauncher =
			fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				// Kullanıcı bir şey seçtiyse ve işlem başarılıysa
				if (result.resultCode == Activity.RESULT_OK) {
					val intentData = result.data  // Galeriden gelen Intent
					val imageUri: Uri? = intentData?.data // Seçilen görselin URI bilgisi

					// Görsel URI null değilse
					imageUri?.let {
						// URI'den bitmap oluştur
						val bitmap = decodeBitmap(it)

						// bitmap oluşturulabildiyse, geri çağırma fonksiyonunu çağır
						bitmap?.let { bmp -> onImageSelected?.invoke(bmp) }
						selectedImage = bitmap
					}
				}
			}
	}

	// Bu fonksiyon izni kontrol eder, gerekiyorsa ister, izin varsa galeriyi açar
	fun checkGalleryPermission(view: View) {
		val currentFragment = fragment ?: return  // Fragment null ise işlem durdurulur

		// Android 13 (API 33) ve üzeri için yeni izin kullanılır
		val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			Manifest.permission.READ_MEDIA_IMAGES
		} else {
			// Daha eski Android sürümleri için eski izin kullanılır
			Manifest.permission.READ_EXTERNAL_STORAGE
		}

		// İzin verilmiş mi kontrol edilir
		if (ContextCompat.checkSelfPermission(currentFragment.requireContext(), permission)
			!= PackageManager.PERMISSION_GRANTED
		) {
			// Eğer izin daha önce verilmemişse

			// Kullanıcı daha önce bu izni reddettiyse, açıklama gösterilmeli mi?
			if (ActivityCompat.shouldShowRequestPermissionRationale(currentFragment.requireActivity(), permission)) {

				// Snackbar ile kullanıcıya açıklama gösterilir, butona tıklarsa izin tekrar istenir
				Snackbar.make(
					view,
					"Galeriye erişmek için izin gerekli",
					Snackbar.LENGTH_INDEFINITE
				).setAction("İzin Ver") {
					permissionLauncher.launch(permission)
				}.show()

			} else {
				// İlk defa isteniyorsa veya açıklama gerekmezse direkt izin istenir
				permissionLauncher.launch(permission)
			}

		} else {
			// İzin zaten verilmiş, direkt galeriyi aç
			openGallery()
		}
	}

	// Bu fonksiyon galeriye gitmek için intent başlatır
	private fun openGallery() {
		// ACTION_PICK intent'i ile sistemin galeri uygulaması açılır
		val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

		// intent, galleryLauncher ile çalıştırılır (önceden register edildi)
		galleryLauncher.launch(intent)
	}

	// Bu fonksiyon URI'den bitmap (resim nesnesi) oluşturur
	private fun decodeBitmap(uri: Uri): Bitmap? {
		val currentFragment = fragment ?: return null

		return try {
			// Eğer Android sürümü 28 ve üzeriyse ImageDecoder kullanılır
			if (Build.VERSION.SDK_INT >= 28) {
				val source = ImageDecoder.createSource(currentFragment.requireActivity().contentResolver, uri)
				ImageDecoder.decodeBitmap(source)
			} else {
				// Daha eski sürümlerde MediaStore yöntemiyle bitmap alınır
				MediaStore.Images.Media.getBitmap(currentFragment.requireActivity().contentResolver, uri)
			}
		} catch (e: Exception) {
			// Hata oluşursa konsola yazdırılır ve null döner
			e.printStackTrace()
			null
		}
	}
}