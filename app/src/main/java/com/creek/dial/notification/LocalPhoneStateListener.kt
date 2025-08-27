package com.creek.dial.notification
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telephony.PhoneStateListener
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.mylibrary.CreekManager
import com.example.proto.Call
import com.example.proto.Enums
import com.example.proto.Message
import com.google.protobuf.ByteString
import java.time.ZonedDateTime



enum class CallType {
    INCOMING, OUTGOING;
}

enum class CallEvent {
    INCOMINGSTART, INCOMINGMISSED, INCOMINGRECEIVED, INCOMINGEND, OUTGOINGEND, OUTGOINGSTART;
}

class LocalPhoneStateListener internal constructor( private val context: Context): PhoneStateListener() {


    private var time: ZonedDateTime? = null
    private var callType: CallType? = null
    private var previousState: Int? = null
    private var RECEIVED_CALL = false

    @RequiresApi(Build.VERSION_CODES.O)
    @Synchronized
    override fun onCallStateChanged(state: Int, incomingNumber: String?) {
        Log.d(
            "LocalPhoneStateListener",
            "********************* - $incomingNumber"
        )

        if (incomingNumber.isNullOrEmpty()) {
            Log.d("LocalPhoneStateListener", "incomingNumber is null or empty, skipping processing.")
            return
        }

        when (state) {
            TelephonyManager.CALL_STATE_IDLE -> {

                if (previousState == TelephonyManager.CALL_STATE_OFFHOOK && callType == CallType.INCOMING) {
                    // Incoming call ended
                    Log.d(
                        "LocalPhoneStateListener",
                        "Phone State event IDLE (INCOMING ENDED) with number - $incomingNumber"
                    )

                    var simSlot: Int = getSimSlotForIncomingCall(context) //获取SIM卡槽信息
                    Log.d("CallReceiver", "Incoming call from123333:  SIM slot: $simSlot")
                    sendToWatch(
                        CallEvent.INCOMINGEND,
                        incomingNumber,simSlot,0,context
                    )
                    sendToWatch(
                        CallEvent.INCOMINGEND,
                        incomingNumber,simSlot,3,context
                    )
                } else if (callType == CallType.OUTGOING) {
                    // Outgoing call ended
                    Log.d(
                        "LocalPhoneStateListener",
                        "Phone State event IDLE (OUTGOING ENDED) with number - $incomingNumber"
                    )

                } else {
                    Log.d(
                        "LocalPhoneStateListener",
                        "Phone State event IDLE (INCOMING MISSED) with number - $incomingNumber"
                    )

                    val simSlot: Int = getSimSlotForIncomingCall(context) //获取SIM卡槽信息
                    Log.d("CallReceiver", "Incoming call from123333:  SIM slot: $simSlot")
                    sendToWatch(CallEvent.INCOMINGMISSED, incomingNumber,simSlot,0,context)
                    sendToWatch(CallEvent.INCOMINGMISSED, incomingNumber,simSlot,3,context)
                }

                callType = null
                previousState = TelephonyManager.CALL_STATE_IDLE
            }

            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.d("LocalPhoneStateListener", "Phone State event STATE_OFF_HOOK")
                // Phone didn't ring, so this is an outgoing call
                if (callType == null)
                    callType = CallType.OUTGOING

                var simSlot: Int = getSimSlotForIncomingCall(context) //获取SIM卡槽信息
                sendToWatch(CallEvent.INCOMINGMISSED,  incomingNumber,simSlot,2,context)

            }

            TelephonyManager.CALL_STATE_RINGING -> {
                Log.d(
                    "LocalPhoneStateListener",
                    "Phone State event PHONE_RINGING number: $incomingNumber"
                )
                val simSlot: Int = getSimSlotForIncomingCall(context) //获取SIM卡槽信息
                sendToWatch(CallEvent.INCOMINGMISSED,  incomingNumber,simSlot,1,context)
            }
        }
    }

    ///来电状态
    private  fun setCallState(type:Int){
        val callStatus = if (type == 0) {
            Enums.call_status.RECEIVED_CALL
        } else {
            Enums.call_status.REJECT_CALL
        }
        CreekManager.sInstance.setCallState(status = callStatus, success = {

        }, failure = {
            c,m ->
        })
    }



    ///消息通知
    private fun sendToWatch(type: CallEvent, phoneNumber: String,simSlotIndex:Int,callState:Int,context: Context) {
        var displayName = ""
        if(type ==CallEvent.INCOMINGMISSED){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                Log.d(
                    "LocalPhoneStateListener",
                    "Have permission"
                )
                if(phoneNumber != ""){
                    displayName = getContactNameByPhoneNumber(context, phoneNumber)
                }

            }else{
                Log.d(
                    "LocalPhoneStateListener",
                    "permission denied"
                )
            }
            Log.d(
                "LocalPhoneStateListener"," displayName: $displayName"
            )
        }

      when (callState) {
            0 -> {
                if (!RECEIVED_CALL){
                    val data = Message.protocol_message_notify_data()
                    data.remindTypeValue = Enums.message_remind_type.Missed_Call_VALUE
                    data.contactText = ByteString.copyFrom((displayName.ifEmpty { phoneNumber }).toByteArray())
                    data.msgContent = ByteString.copyFrom((phoneNumber).toByteArray())
                    val msgId = "$simSlotIndex$phoneNumber"
                    data.msgId = ByteString.copyFrom(msgId.toByteArray())
                    CreekManager.sInstance.setMessageApp(model = data, success = {
                        Log.d(
                            "LocalPhoneStateListener","setMessageApp success"
                        )
                    }, failure = {
                            code: Int, message: String ->
                        Log.d(
                            "LocalPhoneStateListener","setMessageApp failure code:$code message:$message"
                        )
                    })
                }

            }
            1 -> {
                RECEIVED_CALL = false
                 val operate = Call.protocol_call_remind()
                operate.phoneNumber = ByteString.copyFrom((phoneNumber).toByteArray())
                operate.contactName = ByteString.copyFrom((displayName.ifEmpty { phoneNumber }).toByteArray())
                CreekManager.sInstance.setCallReminder(model = operate,success = {
                    Log.d(
                        "LocalPhoneStateListener","setCallReminder success"
                    )
                }, failure = {
                        code: Int, message: String ->
                    Log.d(
                        "LocalPhoneStateListener","setCallReminder failure code:$code message:$message"
                    )
                })
            }
            2 -> {
                RECEIVED_CALL = true
                CreekManager.sInstance.setCallState(status = Enums.call_status.RECEIVED_CALL,success = {
                    Log.d(
                        "LocalPhoneStateListener","setCallState success"
                    )
                }, failure = {
                        code: Int, message: String ->
                    Log.d(
                        "LocalPhoneStateListener","setCallState failure code:$code message:$message"
                    )
                })
            }
            3 -> {
                CreekManager.sInstance.setCallState(status = Enums.call_status.REJECT_CALL,success = {
                    Log.d(
                        "LocalPhoneStateListener","setCallState success"
                    )
                }, failure = {
                        code: Int, message: String ->
                    Log.d(
                        "LocalPhoneStateListener","setCallState failure code:$code message:$message"
                    )
                })
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getSimSlotForIncomingCall(context: Context): Int {
        val permission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_PHONE_STATE
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val activeList = subscriptionManager.activeSubscriptionInfoList
            if (!activeList.isNullOrEmpty()) {
                for (info in activeList) {
                    val subId = info.subscriptionId
                    val telephonyManager = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).createForSubscriptionId(subId)
                    if (telephonyManager.callState == TelephonyManager.CALL_STATE_RINGING) {
                        // 找到正在响铃的卡槽
                        return info.simSlotIndex
                    }
                }
                // 没有检测到正在响铃的，默认返回主卡或第一个
                return activeList[0].simSlotIndex
            }
        }
        return 9
    }

    @SuppressLint("Range")
    fun getContactNameByPhoneNumber(context: Context, phoneNumber: String): String {
        val uri: Uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon().appendEncodedPath(phoneNumber).build()
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
            }
        }
        return ""
    }
}