//package com.creek.dial.notification
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Build
//import android.telephony.PhoneStateListener
//import android.telephony.SubscriptionInfo
//import android.telephony.SubscriptionManager
//import android.telephony.TelephonyManager
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.core.content.ContextCompat
//import com.example.creek_blue_manage.LocalPhoneStateListener
//
//
//class CreekPhoneStateReceiver : BroadcastReceiver() {
//    private var telephony: TelephonyManager? = null
//
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
//    override fun onReceive(context: Context, intent: Intent) {
//        if (phoneStateBackgroundListener == null) {
//            phoneStateBackgroundListener = LocalPhoneStateListener(context)
//            telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            telephony!!.listen(phoneStateBackgroundListener, PhoneStateListener.LISTEN_CALL_STATE)
//
//        }
//
//    }
//
//
//
//    companion object {
//        @SuppressLint("StaticFieldLeak")
//        private var phoneStateBackgroundListener: LocalPhoneStateListener? = null
//    }
//}