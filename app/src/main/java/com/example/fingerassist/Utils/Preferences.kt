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

    fun saveUser(name: String) {
        storage.edit().putString(SHARED_USER, name).apply()
    }

    fun saveRemember(remember: Boolean) {
        storage.edit().putBoolean(SHARED_REMEMBER, remember).apply()
    }

    /*fun saveLat(lat:String){
        storage.edit().putString("lat",lat).apply()
    }

    fun saveLng(lng:String){
        storage.edit().putString("lng",lng).apply()
    }*/

    fun saveLatLng(latLng: Set<String>) {
        storage.edit().putStringSet("latlng", latLng).apply()
    }

    fun saveAxis1(axis: Set<String>) {
        storage.edit().putStringSet("axis1", axis).apply()
    }

    fun saveAxis2(axis: Set<String>) {
        storage.edit().putStringSet("axis2", axis).apply()
    }

    fun saveAxis3(axis: Set<String>) {
        storage.edit().putStringSet("axis3", axis).apply()
    }

    fun saveAxis4(axis: Set<String>) {
        storage.edit().putStringSet("axis4", axis).apply()
    }

    fun getName(): String {
        return storage.getString(SHARED_USER, "")!!
    }

    fun getRemember(): Boolean {
        return storage.getBoolean(SHARED_REMEMBER, false)
    }

    //coord lugar
    fun getLat(): String {
        Log.d("localizacion",storage.getStringSet("latlng", setOf()).toString())
        return storage.getStringSet("latlng", setOf())!!.toList()[1]
    }

    fun getLng(): String {
        return storage.getStringSet("latlng", setOf())!!.toList()[0]
    }

    //axis1
    fun getAxis1Lat(): String {
        return storage.getStringSet("axis1", setOf())!!.toList()[1]
    }

    fun getAxis1Lng(): String {
        return storage.getStringSet("axis1", setOf())!!.toList()[0]
    }

    //axis2
    fun getAxis2Lat(): String {
        return storage.getStringSet("axis2", setOf())!!.toList()[1]
    }

    fun getAxis2Lng(): String {
        return storage.getStringSet("axis2", setOf())!!.toList()[0]
    }

    //axis3
    fun getAxis3Lat(): String {
        return storage.getStringSet("axis3", setOf())!!.toList()[0]
    }

    fun getAxis3Lng(): String {
        return storage.getStringSet("axis3", setOf())!!.toList()[1]
    }

    //axis4
    fun getAxis4Lat(): String {
        return storage.getStringSet("axis4", setOf())!!.toList()[0]
    }

    fun getAxis4Lng(): String {
        return storage.getStringSet("axis4", setOf())!!.toList()[1]
    }
}