package com.ncorp.yemektarifleri.roomdb
import androidx.room.Database
import androidx.room.RoomDatabase
import com.ncorp.yemektarifleri.model.Tarif

@Database(entities = [Tarif::class], version = 1)
abstract class TarifDB : RoomDatabase() {
	abstract fun tarifDao(): TarifDao
}