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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //İzin verilmemiş, izin istenecek
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    //snackbar gösterilecek
                    Snackbar.make(
                        view,
                        "Galeriye gitmek için izin gerekli",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(
                            "İzin Ver",
                            View.OnClickListener {
                                //izin istenecek
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                            }

                        ).show()

                } else {
                    //İzin istenecek
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                }

            } else {
                //izin zaten verilmiş izin istemeden galeriye git
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }


        } else {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //İzin verilmemiş, izin istenecek
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    //snackbar gösterilecek
                    Snackbar.make(
                        view,
                        "Galeriye gitmek için izin gerekli",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(
                            "İzin Ver",
                            View.OnClickListener {
                                //izin istenecek
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                            }

                        ).show()

                } else {
                    //İzin istenecek
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }

            } else {
                //izin zaten verilmiş izin istemeden galeriye git
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }


    }

    fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        secilenGorsel = intentFromResult.data
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(
                                    requireActivity().contentResolver,
                                    secilenGorsel!!
                                )
                                secilenBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(secilenBitmap)

                            } else {
                                secilenBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    secilenGorsel
                                )
                                binding.imageView.setImageBitmap(secilenBitmap)

                            }
                        } catch (e: Exception) {
                            println(e.localizedMessage)
                        }


                    }

                }
            }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            { result ->
                if (result) {
                    //izin verildi
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //izin verilmedi
                    Toast.makeText(requireContext(), "İzin verilmedi", Toast.LENGTH_LONG).show()
                }

            })

    }
}
