package com.creek.dial.notification
import android.app.Notification
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mylibrary.CreekManager
import com.example.proto.Enums
import com.example.proto.Message
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils
import com.google.protobuf.ByteString
import java.util.Base64

class MyNotificationListenerService : NotificationListenerService() {



    private val replyableNotifications = mutableMapOf<String, ReplyableNotification>()

    companion object {
        private var instance: MyNotificationListenerService? = null

        fun getInstance(): MyNotificationListenerService? {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text")
        val isForeground = (sbn.notification.flags and Notification.FLAG_FOREGROUND_SERVICE) != 0
        val isFlagNOClear = sbn.notification.flags and Notification.FLAG_NO_CLEAR != 0
        if (isForeground && isFlagNOClear) {
            Log.i("NotificationListener", "onNotificationPosted----filter-flags:-${sbn.notification.flags}")
            return
        }
        Log.i("NotificationListener", "Notification from $packageName: $title - $text")
        var msgId = ""
        if (notification.actions != null) {
            for (action in notification.actions) {
                if (action.remoteInputs != null) {
                    for (remoteInput in action.remoteInputs) {
                        if (remoteInput != null) {
                            //Generate short hash keys
                            val shortKey = HashUtil.generateShortHash(sbn.key)
                            msgId = shortKey
                            replyableNotifications[shortKey] = ReplyableNotification(
                                packageName = packageName,
                                notificationId = sbn.id,
                                key = sbn.key,
                                action = action,
                                remoteInput = remoteInput,
                                title = title,
                                text = text
                            )
                        }
                    }
                }
            }
        }
        if(packageName.contains("com.whatsapp")){
             var data = Message.protocol_message_notify_data()
            data.remindTypeValue = Enums.message_remind_type.Whatsapp_VALUE
            data.contactText = ByteString.copyFrom((title?:"").toByteArray())
            data.msgContent = ByteString.copyFrom((text.toString()).toByteArray())
            data.msgId = ByteString.copyFrom(msgId.toByteArray())
            CreekManager.sInstance.setMessageApp(model = data, success = {

            }, failure = {
                code: Int, message: String ->

            })
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        // 通知被移除时，从字典中删除
        replyableNotifications.remove(sbn.key)
    }

    fun getReplyableNotification(key: String): ReplyableNotification? {
        return replyableNotifications[key]
    }

    fun getAllReplyableNotifications(): List<ReplyableNotification> {
        return replyableNotifications.values.toList()
    }

    fun sendReply(key: String, replyMessage: String) {
        val notification = replyableNotifications[key]
        if (notification != null) {
            val intent = Intent()
            val bundle = Bundle()
            bundle.putCharSequence(notification.remoteInput.resultKey, replyMessage)
            RemoteInput.addResultsToIntent(arrayOf(notification.remoteInput), intent, bundle)
            try {
                notification.action.actionIntent.send(this, 0, intent)
                Log.i("NotificationListener", "Replied to ${notification.packageName} with message: $replyMessage")
                // Optionally, remove the notification from the map after replying
                replyableNotifications.remove(key)
            } catch (e: PendingIntent.CanceledException) {
                Log.e("NotificationListener", "Failed to send reply", e)
            }
        } else {
            Log.e("NotificationListener", "Notification with key $key not found")
        }
    }
}

data class ReplyableNotification(
    val packageName: String,
    val notificationId: Int,
    val key: String,
    val action: Notification.Action,
    val remoteInput: RemoteInput,
    val title: String?,
    val text: CharSequence?
)

object HashUtil {
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateShortHash(input: String): String {
        val md5Hex = DigestUtils.md5Hex(input)
        return Base64.getUrlEncoder().encodeToString(md5Hex.toByteArray()).substring(0, 11)
    }
}