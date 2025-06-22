package com.ncorp.yemektarifleri.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tarif(

	@ColumnInfo(name = "isim")
	var isim: String,
	@ColumnInfo(name = "malzemeler")
	var malzemeler: String,
	@ColumnInfo(name = "gorsel")
	var gorsel: ByteArray

){
	@PrimaryKey(autoGenerate = true)
	var id = 0
}
