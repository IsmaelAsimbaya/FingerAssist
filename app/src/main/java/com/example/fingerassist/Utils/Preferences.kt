package com.example.fingerassist.Utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.maps.model.LatLng

class Preferences(val context: Context) {

    val SHARED_NAME = "sessions"
    val SHARED_USER = "user"
    val SHARED_REMEMBER = "remember"

    val storage = context.getSharedPreferences(SHARED_NAME, 0)

    fun getSharedPreference(): SharedPreferences {
        return storage
    }

    //user
    fun saveUser(name: String) {
        storage.edit().putString(SHARED_USER, name).apply()
    }

    fun getName(): String {
        return storage.getString(SHARED_USER, "")!!
    }

    //remember
    fun saveRemember(remember: Boolean) {
        storage.edit().putBoolean(SHARED_REMEMBER, remember).apply()
    }

    fun getRemember(): Boolean {
        return storage.getBoolean(SHARED_REMEMBER, false)
    }

    //codigoFecha
    fun saveCodigoFecha(codigo:String){
        storage.edit().putString("codigoFecha",codigo).apply()
    }

    fun getCodigoFecha():String{
        return storage.getString("codigoFecha","12_10_1492")!!
    }

    //marcacionDiaria
    fun saveMarcacionDiaria(marcDiaria:Boolean){
        storage.edit().putBoolean("marcDiaria", marcDiaria).apply()
    }

    fun getMarcacionDiaria():Boolean{
        return storage.getBoolean("marcDiaria",false)
    }

    //marcacionEntrada
    fun saveMarcacionEntrada(marcEntrada: Boolean){
        storage.edit().putBoolean("marcEntrada",marcEntrada).apply()
    }

    fun getMarcacionesEntrada():Boolean{
        return storage.getBoolean("marcEntrada",false)
    }

    //marcacionSalida
    fun saveMarcacionSalida(marcSalida: Boolean){
        storage.edit().putBoolean("marcSalida",marcSalida).apply()
    }

    fun getMarcacionesSalida():Boolean{
        return storage.getBoolean("marcSalida",false)
    }

    //horario
    fun saveHorario(horario: String){
        storage.edit().putString("horario",horario).apply()
    }

    fun getHorario():String{
        return storage.getString("horario","")!!
    }

    //coord lugar
    fun saveLatLng(latLng: Set<String>) {
        storage.edit().putStringSet("latlng", latLng).apply()
    }

    fun getLat(): String {
        Log.d("localizacion",storage.getStringSet("latlng", setOf()).toString())
        return storage.getStringSet("latlng", setOf())!!.toList()[1]
    }

    fun getLng(): String {
        return storage.getStringSet("latlng", setOf())!!.toList()[0]
    }

    //axis1
    fun saveAxis1(axis: Set<String>) {
        storage.edit().putStringSet("axis1", axis).apply()
    }

    fun getAxis1Lat(): String {
        return storage.getStringSet("axis1", setOf())!!.toList()[1]
    }

    fun getAxis1Lng(): String {
        return storage.getStringSet("axis1", setOf())!!.toList()[0]
    }

    //axis2
    fun saveAxis2(axis: Set<String>) {
           storage.edit().putStringSet("axis2", axis).apply()
    }

    fun getAxis2Lat(): String {
        return storage.getStringSet("axis2", setOf())!!.toList()[1]
    }

    fun getAxis2Lng(): String {
        return storage.getStringSet("axis2", setOf())!!.toList()[0]
    }

    //axis3
    fun saveAxis3(axis: Set<String>) {
        storage.edit().putStringSet("axis3", axis).apply()
    }

    fun getAxis3Lat(): String {
        return storage.getStringSet("axis3", setOf())!!.toList()[0]
    }

    fun getAxis3Lng(): String {
        return storage.getStringSet("axis3", setOf())!!.toList()[1]
    }

    //axis4
    fun saveAxis4(axis: Set<String>) {
        storage.edit().putStringSet("axis4", axis).apply()
    }

    fun getAxis4Lat(): String {
        return storage.getStringSet("axis4", setOf())!!.toList()[0]
    }

    fun getAxis4Lng(): String {
        return storage.getStringSet("axis4", setOf())!!.toList()[1]
    }
}