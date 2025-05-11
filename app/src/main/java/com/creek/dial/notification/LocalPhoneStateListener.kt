//package com.example.creek_blue_manage
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Build
//import android.provider.ContactsContract
//import android.telephony.PhoneStateListener
//import android.telephony.SmsManager
//import android.telephony.SubscriptionManager
//import android.telephony.TelephonyManager
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.core.content.ContextCompat
//import com.example.mylibrary.CreekManager
//import com.example.proto.Enums
//import com.example.proto.Message
//import com.google.protobuf.ByteString
//import java.time.Duration
//import java.time.ZonedDateTime
//import java.util.HashMap
//
//
//enum class CallType {
//    INCOMING, OUTGOING;
//}
//
//enum class CallEvent {
//    INCOMINGSTART, INCOMINGMISSED, INCOMINGRECEIVED, INCOMINGEND, OUTGOINGEND, OUTGOINGSTART;
//}
//
//class LocalPhoneStateListener internal constructor( private val context: Context): PhoneStateListener() {
//
//
//    private var time: ZonedDateTime? = null
//    private var callType: CallType? = null
//    private var previousState: Int? = null
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    @Synchronized
//    override fun onCallStateChanged(state: Int, incomingNumber: String?) {
//        when (state) {
//            TelephonyManager.CALL_STATE_IDLE -> {
//                val duration = Duration.between(time ?: ZonedDateTime.now(), ZonedDateTime.now())
//
//                if (previousState == TelephonyManager.CALL_STATE_OFFHOOK && callType == CallType.INCOMING) {
//                    // Incoming call ended
//                    Log.d(
//                        "LocalPhoneStateListener",
//                        "Phone State event IDLE (INCOMING ENDED) with number - $incomingNumber"
//                    )
//
//                    var simSlot: Int = getSimSlotForIncomingCall(context) //获取SIM卡槽信息
//                    Log.d("CallReceiver", "Incoming call from123333:  SIM slot: $simSlot")
//                    sendToWatch(
//                        CallEvent.INCOMINGEND,
//                        duration.toMillis() / 1000,
//                        incomingNumber!!,simSlot,context
//                    )
//                } else if (callType == CallType.OUTGOING) {
//                    // Outgoing call ended
//                    Log.d(
//                        "LocalPhoneStateListener",
//                        "Phone State event IDLE (OUTGOING ENDED) with number - $incomingNumber"
//                    )
//
//                } else {
//                    Log.d(
//                        "LocalPhoneStateListener",
//                        "Phone State event IDLE (INCOMING MISSED) with number - $incomingNumber"
//                    )
//
//                    var simSlot: Int = getSimSlotForIncomingCall(context) //获取SIM卡槽信息
//                    Log.d("CallReceiver", "Incoming call from123333:  SIM slot: $simSlot")
//
//                    sendToWatch(CallEvent.INCOMINGMISSED, 0, incomingNumber!!,simSlot,context)
//                }
//
//                callType = null
//                previousState = TelephonyManager.CALL_STATE_IDLE
//            }
//
//            TelephonyManager.CALL_STATE_OFFHOOK -> {
//                Log.d("LocalPhoneStateListener", "Phone State event STATE_OFF_HOOK")
//                // Phone didn't ring, so this is an outgoing call
//                if (callType == null)
//                    callType = CallType.OUTGOING
//
//            }
//
//            TelephonyManager.CALL_STATE_RINGING -> {
//                Log.d(
//                    "LocalPhoneStateListener",
//                    "Phone State event PHONE_RINGING number: $incomingNumber"
//                )
//            }
//        }
//    }
//
//    private fun sendToWatch(type: CallEvent, duration: Long, phoneNumber: String,simSlotIndex:Int,context: Context) {
//        var displayName:String? = null
//        if(type ==CallEvent.INCOMINGMISSED){
//             if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//                 Log.d(
//                     "LocalPhoneStateListener",
//                     "Have permission"
//                 )
//                 displayName = getContactNameByPhoneNumber(context, phoneNumber)
//             }else{
//                 Log.d(
//                     "LocalPhoneStateListener",
//                     "permission denied"
//                 )
//             }
//         }
//
//        var data = Message.protocol_message_notify_data()
//        data.remindTypeValue = Enums.message_remind_type.Missed_Call_VALUE
//        data.contactText = ByteString.copyFrom((displayName?:phoneNumber).toByteArray())
//        data.msgContent = ByteString.copyFrom((phoneNumber).toByteArray())
//        var msgId = "$simSlotIndex$phoneNumber"
//        data.msgId = ByteString.copyFrom(msgId.toByteArray())
//        CreekManager.sInstance.setMessageApp(model = data, success = {
//
//        }, failure = {
//                code: Int, message: String ->
//
//        })
//
//    }
//
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
//    private fun getSimSlotForIncomingCall(context: Context): Int {
//        val smsSelfPermission = ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.READ_PHONE_STATE
//        )
//        if(smsSelfPermission == PackageManager.PERMISSION_GRANTED){
//            val subscriptionManager =context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
//            val activeSubscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
//            val telephonyManager =
//                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            for (subscriptionInfo in activeSubscriptionInfoList) {
//                val subId = subscriptionInfo.subscriptionId
//                val simSlotIndex = subscriptionInfo.simSlotIndex
//                println("subId--$subId---simSlotIndex$simSlotIndex")
//                val phoneState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    telephonyManager.createForSubscriptionId(subId).callState
//                } else {
//                    telephonyManager.callState
//                }
//                if (phoneState == TelephonyManager.CALL_STATE_RINGING) {
//                    if (subscriptionInfo.simSlotIndex == 0) {
//                        Log.i("IncomingCallReceiver", "Incoming call on SIM 1: ")
//                        return simSlotIndex
//                    } else if (subscriptionInfo.simSlotIndex == 1) {
//                        Log.i("IncomingCallReceiver", "Incoming call on SIM 2: ")
//                        return simSlotIndex
//                    }
//                }
//            }
//        }
//        return 9 // Unknown slot
//    }
//
//    @SuppressLint("Range")
//    fun getContactNameByPhoneNumber(context: Context, phoneNumber: String): String {
//        val uri: Uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon().appendEncodedPath(phoneNumber).build()
//        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
//
//        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
//            if (cursor.moveToFirst()) {
//                return cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
//            }
//        }
//        return ""
//    }
//}