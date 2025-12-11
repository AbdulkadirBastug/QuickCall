package com.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise

class VolumeModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "VolumeModule"
    }

    @ReactMethod
    fun savePhoneNumber(number: String) {
        val prefs = reactContext.getSharedPreferences("QuickCallPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("phoneNumber", number).apply()
    }

    @ReactMethod
    fun getPhoneNumber(promise: Promise) {
        try {
            val prefs = reactContext.getSharedPreferences("QuickCallPrefs", Context.MODE_PRIVATE)
            val number = prefs.getString("phoneNumber", "")
            promise.resolve(number)
        } catch (e: Exception) {
            promise.reject("GET_NUMBER_ERROR", e)
        }
    }

    @ReactMethod
    fun checkOverlayPermission(promise: Promise) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            promise.resolve(Settings.canDrawOverlays(reactContext))
        } else {
            promise.resolve(true)
        }
    }

    @ReactMethod
    fun requestOverlayPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(reactContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + reactContext.packageName))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                reactContext.startActivity(intent)
            }
        }
    }

    @ReactMethod
    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        reactContext.startActivity(intent)
    }
}
