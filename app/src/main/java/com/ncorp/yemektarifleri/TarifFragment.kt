package com.ncorp.yemektarifleri

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ncorp.yemektarifleri.databinding.TarifFragmentBinding


class TarifFragment : Fragment() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String> //İzin istemek için
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent> //Galeriye gitmek için
    private var _binding: TarifFragmentBinding? = null
    private val binding get() = _binding!!
    private var secilenGorsel: Uri? = null
    private var secilenBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = TarifFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnClickListener { image(it) }
        binding.saveButton.setOnClickListener { save(it) }
        binding.deleteButton.setOnClickListener { delete(it) }

        arguments?.let {
            val bilgi = TarifFragmentArgs.fromBundle(it).bilgi
            if (bilgi.equals("yeni")) {//Yeni Tarif eklenecek
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

    fun save(view: View) {}
    fun delete(view: View) {}
    fun image(view: View) {
        permissionController(view)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    //////////////////////////////////////////////////////////////////////
    fun permissionController(view: View) {
        // Eğer cihazın Android sürümü 13 (API 33) veya daha üzeriyse
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            // Android 13 ve üstü için: READ_MEDIA_IMAGES izni verilmiş mi kontrol ediliyor
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // İzin verilmemiş

                // Kullanıcı daha önce reddettiyse, neden izin istendiğini açıklamak için gösterim yapılır mı?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    // Kullanıcıya Snackbar ile açıklama gösteriliyor ve bir buton sunuluyor
                    Snackbar.make(
                        view,
                        "Galeriye gitmek için izin gerekli",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(
                            "İzin Ver",
                            View.OnClickListener {
                                // Kullanıcı butona tıklarsa: izin isteme işlemi başlatılıyor
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }

                        ).show()

                } else {
                    // Kullanıcı daha önce hiçbir tepki vermemişse, doğrudan izin istenir
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }

            } else {
                // İzin zaten verilmişse, doğrudan galeri açılır
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                // Galeri başlatıcıya intent gönderilir
                activityResultLauncher.launch(intentToGallery)
            }

        } else {
            // Android 12 ve altı sürümler için: READ_EXTERNAL_STORAGE kullanılır

            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // İzin verilmemiş

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    // Kullanıcıya neden izin gerektiği anlatılıyor
                    Snackbar.make(
                        view,
                        "Galeriye gitmek için izin gerekli",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(
                            "İzin Ver",
                            View.OnClickListener {
                                // Kullanıcı butona tıklarsa izin istenir
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }

                        ).show()

                } else {
                    // Kullanıcıdan direkt olarak izin isteniyor
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            } else {
                // İzin zaten verilmiş, doğrudan galeriye git
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }


    fun registerLauncher() {
        // Galeri açma işlemi için ActivityResultLauncher tanımlanıyor
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                // Eğer kullanıcı başarılı şekilde bir şey seçtiyse (RESULT_OK)
                if (result.resultCode == Activity.RESULT_OK) {
                    val intentFromResult = result.data // Galeriden dönen veri alınır
                    if (intentFromResult != null) {
                        secilenGorsel = intentFromResult.data // Seçilen görselin URI'si alınır

                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                // Android 9 (API 28) ve sonrası için: ImageDecoder kullanılır
                                val source = ImageDecoder.createSource(
                                    requireActivity().contentResolver,
                                    secilenGorsel!!
                                )
                                // URI'den bitmap'e çevirme işlemi
                                secilenBitmap = ImageDecoder.decodeBitmap(source)
                                // Görsel ImageView'a atanıyor
                                binding.imageView.setImageBitmap(secilenBitmap)

                            } else {
                                // Android 8 ve altı için: MediaStore yöntemiyle bitmap alınıyor
                                secilenBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    secilenGorsel
                                )
                                // Görsel ImageView'da gösteriliyor
                                binding.imageView.setImageBitmap(secilenBitmap)
                            }

                        } catch (e: Exception) {
                            // Hata olursa konsola yazdırılıyor
                            println(e.localizedMessage)
                        }
                    }
                }
            }

        // İzin isteme işlemi için launcher tanımlanıyor
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->

            // Eğer kullanıcı izni VERDİYSE
            if (result) {
                // Galeriye gitmek için intent oluştur
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery) // Galeriyi aç
            } else {
                // Kullanıcı izni REDDETTİYSE uyarı göster
                Toast.makeText(requireContext(), "İzin verilmedi", Toast.LENGTH_LONG).show()
            }
        }
    }

}
