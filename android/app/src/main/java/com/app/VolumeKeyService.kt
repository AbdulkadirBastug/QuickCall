package com.app

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.telecom.TelecomManager
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.app.NotificationChannel

class VolumeKeyService : AccessibilityService() {

    private var lastClickTime: Long = 0
    private var clickCount = 0
    private val CLICK_THRESHOLD = 2000 // 2 seconds to click twice
    private var wakeLock: PowerManager.WakeLock? = null
    private val CHANNEL_ID = "QuickCallServiceChannel"

    override fun onServiceConnected() {
        super.onServiceConnected()
        
        try {
            // Basic config
            val info = android.accessibilityservice.AccessibilityServiceInfo()
            info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            info.feedbackType = android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_GENERIC
            info.flags = android.accessibilityservice.AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS or 
                         android.accessibilityservice.AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            info.notificationTimeout = 100
            this.serviceInfo = info

            // WakeLock
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "QuickCall::VolumeKeyService")
            wakeLock?.acquire(10*60*1000L /*10 minutes*/)

            // Notification
            createNotificationChannel()
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("QuickCall Active")
                .setContentText("Service Running")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val keyCode = event.keyCode
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < CLICK_THRESHOLD) {
                    clickCount++
                } else {
                    clickCount = 1
                }
                lastClickTime = currentTime

                if (clickCount == 2) {
                    makeCall()
                    clickCount = 0
                }
                return false
            }
        }
        return super.onKeyEvent(event)
    }

    private fun makeCall() {
        try {
            val prefs = getSharedPreferences("QuickCallPrefs", Context.MODE_PRIVATE)
            val rawNumber = prefs.getString("phoneNumber", "") ?: ""
            val phoneNumber = rawNumber.replace(Regex("[^0-9+]"), "")

            if (phoneNumber.isEmpty()) {
                return
            }

            // Try TelecomManager first (Best for background)
            val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val uri = Uri.fromParts("tel", phoneNumber, null)
            val extras = Bundle()
            extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)

            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                telecomManager.placeCall(uri, extras)
            } else {
                // Fallback to Intent
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Final fallback: Dialer
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:123") 
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e2: Exception) {
                // Ignore
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "QuickCall Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            wakeLock?.release()
        } catch (e: Exception) {}
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
