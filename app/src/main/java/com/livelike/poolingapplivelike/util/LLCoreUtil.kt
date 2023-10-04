package com.livelike.poolingapplivelike.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.threeten.bp.Duration
import org.threeten.bp.format.DateTimeParseException


object LLCoreUtil {


    
//    //TF1
    const val LIVELIKE_CLIENT_ID = "skoaC6EnpAJNws4HVVD2WrNcLECxQ2TSdgw3WpnM"
    const val PROGRAM_ID = "5f0f0a74-3798-47ed-9246-93e48230857b"
    
    
    
    private const val PREF_ROOT = "com.ll_demo.pref.root"
    private const val KEY_USER_TOKEN = "key.ll_demo.user_token"
    
    private const val liveLikeUserKey = "${LIVELIKE_CLIENT_ID}_${KEY_USER_TOKEN}"
    
    private fun<T> SharedPreferences.putValue(key:String, value:T?){
        val editor = this.edit()
        when (value) {
            is String? -> {editor.putString(key, value)}
            is Int ->{ editor.putInt(key, value)}
            is Boolean ->  { editor.putBoolean(key, value)}
            is Float ->  { editor.putFloat(key, value) }
            is Long ->  { editor.putLong(key, value) }
            else -> throw UnsupportedOperationException("Not implemented yet!")
        }
        editor.apply()
    }
    
    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_ROOT, Context.MODE_PRIVATE)
    }
    
    fun saveToken(context: Context, value: String?) {
        getSharedPreference(context).putValue(liveLikeUserKey, value)
    }
    
    fun getToken(context: Context): String? {
        return getSharedPreference(context).getString(liveLikeUserKey, null)
    }
    
    
    fun String.parseDuration() : Long {
        var timeout = 15000L
        try {
            timeout = Duration.parse(this).toMillis()
        } catch (e : DateTimeParseException) {
            Log.e("Error", "Duration $this can't be parsed.")
        }
        return timeout
    }
    
}

