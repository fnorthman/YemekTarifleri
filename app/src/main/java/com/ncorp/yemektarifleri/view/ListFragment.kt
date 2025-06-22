package com.ncorp.yemektarifleri.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.ncorp.yemektarifleri.adapter.TarifAdapter
import com.ncorp.yemektarifleri.databinding.ListFragmentBinding
import com.ncorp.yemektarifleri.model.Tarif
import com.ncorp.yemektarifleri.roomdb.TarifDB
import com.ncorp.yemektarifleri.roomdb.TarifDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListFragment : Fragment() {

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: TarifDB
    private lateinit var tarifDao: TarifDao
    private val mDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db=Room.databaseBuilder(requireContext(),TarifDB::class.java,"Tarifler").build()
        tarifDao=db.tarifDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tarifRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.floatingActionButton.setOnClickListener {
            YeniEkle(it)

        }
        verileriAl()
    }
    private fun verileriAl(){
        mDisposable.add(tarifDao.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse))
    }
    private fun handleResponse(tarifler:List<Tarif>){
        tarifler.forEach {

            println(tarifler.toString())
        }
        val adapter=TarifAdapter(tarifler)
        binding.tarifRecyclerView.adapter=adapter
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mDisposable.clear()
    }
    fun YeniEkle (view: View){
        val action = ListFragmentDirections.actionListFragmentToTarifFragment("yeni",-1)
        Navigation.findNavController(view).navigate(action)
    }

    }









