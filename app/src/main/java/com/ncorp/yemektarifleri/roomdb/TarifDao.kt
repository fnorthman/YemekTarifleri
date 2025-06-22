package com.ncorp.yemektarifleri.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ncorp.yemektarifleri.model.Tarif
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface TarifDao {

	@Query("SELECT * FROM Tarif")
	fun getAll(): Flowable<List<Tarif>>

	@Query("SELECT * FROM Tarif WHERE id = :id")
	fun findById(id: Int): Flowable<Tarif>

	@Insert
	fun ınsert(tarif: Tarif): Completable

	@Delete
	fun delete(tarif: Tarif) : Completable

}