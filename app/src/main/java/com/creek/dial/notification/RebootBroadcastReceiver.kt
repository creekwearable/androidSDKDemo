package com.creek.dial.notification


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class RebootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_REBOOT, Intent.ACTION_BOOT_COMPLETED -> {
                Log.i("NotificationListener", "RebootBroadcastReceiver, 手机--${intent.action.toString()}")
            }
            else -> {
                Log.i("NotificationListener", intent.action.toString())
            }
        }
    }
}