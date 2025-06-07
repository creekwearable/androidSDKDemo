package com.creek.dial

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.creek.dial.customDial.CustomDialScreen
import com.creek.dial.dial.DialScreen
import com.creek.dial.music.MusicUploadScreen
import com.creek.dial.navigation.Dial
import com.creek.dial.navigation.SdkFunction
import com.creek.dial.navigation.tabRowScreens
import com.creek.dial.notification.MyNotificationListenerService
import com.creek.dial.scanDevice.ScanDeviceScreen
import com.creek.dial.sdkFunction.SdkFunction
import com.creek.dial.sendCommand.SendCommandScreen
import com.creek.dial.ui.theme.Creek_dial_androidTheme
import com.example.creek_blue_manage.LocalPhoneStateListener
import com.example.model.EphemerisGPSModel
import com.example.mylibrary.BluetoothStateType
import com.example.mylibrary.CancelAutoConnectType
import com.example.mylibrary.ConnectionStatus
import com.example.mylibrary.CreekClientType
import com.example.mylibrary.CreekManager
import com.example.mylibrary.eventIdType
import com.example.proto.Call
import com.example.proto.Enums
import com.example.proto.Message
import com.example.xfyun_speech.XfyunSpeechPlugin
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.notification_listener_util.music.CreekMediaControllerUtils
import kotlinx.coroutines.delay
import okio.Timeout
import java.util.Timer
import java.util.TimerTask
import kotlin.math.round

class MainActivity : ComponentActivity(){

    private val REQUEST_BLUETOOTH_PERMISSION = 1

    @Composable
    fun OneTimeTimer(onTimeout: () -> Unit) {
        LaunchedEffect(Unit) {
            delay(1000) // 延迟1秒
            onTimeout() // 执行一次
        }
    }


    private val lifecycleObserver = object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResumed() {
            // Activity resumed (i.e., app in foreground)
            println("App in foreground")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPaused() {
            // Activity paused (i.e., app potentially in background)
            println("App potentially in background")
            CreekManager.sInstance.monitorPhone()
        }
    }
    fun isNotificationListenerEnabled(context: Context): Boolean {
        val packageNames = NotificationManagerCompat.getEnabledListenerPackages(context)
        return packageNames.contains(context.packageName)
    }

    fun isConnected(): Boolean {
        return CreekManager.sInstance.connectStatus in setOf(ConnectionStatus.CONNECT, ConnectionStatus.SYNC, ConnectionStatus.SYNC_COMPLETE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(

        )
        super.onCreate(savedInstanceState)
        var mAudioManager: AudioManager? = null
        if(isNotificationListenerEnabled(applicationContext)){
            mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            CreekMediaControllerUtils.getInstance().initMediaSessionManager(applicationContext, mAudioManager,this::class.java.name)
        }
        CreekManager.sInstance.creekRegister(this, completed = {

            CreekManager.sInstance.initSDK()
            CreekManager.sInstance.listenDeviceState { status, deviceName ->
                Log.w("123456", "$status++++$deviceName")
            }
//            CreekManager.sInstance.externalConnect(id = "F4:4E:FD:A6:23:C8", connect = {
//                Log.w("123456", "🌹🌹🌹" + it.toString())
//            })
            CreekManager.sInstance.noticeUpdateListen {
                Log.w("123456", it.toString())

                if (it.eventId == eventIdType.EVENT_ID_MUSIC_CONTROL){
                    when(it.eventKey){
                        0 ->{
                            CreekMediaControllerUtils.getInstance().startPlayMusic()
                        }
                        1 ->{
                            CreekMediaControllerUtils.getInstance().pauseMusic()
                        }
                        2 ->{
                            CreekMediaControllerUtils.getInstance().previousSong()
                        }
                        3 ->{
                            CreekMediaControllerUtils.getInstance().nextSong()
                        }
                        4 ->{
                            Log.w("123456", CreekMediaControllerUtils.getInstance().getVolume().toString())
                            val maxVolume: Int = CreekMediaControllerUtils.getInstance().mMediaController!!.playbackInfo?.maxVolume ?: mAudioManager?.getStreamMaxVolume(
                                AudioManager.STREAM_MUSIC
                            ) ?: 0

                            CreekMediaControllerUtils.getInstance().setVolume(CreekMediaControllerUtils.getInstance().getVolume().toDouble()/maxVolume + 0.1)
                        }
                        5 ->{
                            val maxVolume: Int = CreekMediaControllerUtils.getInstance().mMediaController!!.playbackInfo?.maxVolume ?: mAudioManager?.getStreamMaxVolume(
                                AudioManager.STREAM_MUSIC
                            ) ?: 0
                            Log.w("123456", CreekMediaControllerUtils.getInstance().getVolume().toString())
                            CreekMediaControllerUtils.getInstance().setVolume(CreekMediaControllerUtils.getInstance().getVolume().toDouble()/maxVolume - 0.1)
                        }
                    }
                }
            }
            CreekManager.sInstance.exceptionListen {
                Log.w("123456", it)
            }
            CreekManager.sInstance.eventReportListen {

            }
            CreekManager.sInstance.bluetoothStateListen { state : BluetoothStateType ->
                if (state == BluetoothStateType.ON){
                    Log.w("123456", "Bluetooth is currently powered on and available to use")
                }else if (state == BluetoothStateType.OFF){
                    Log.w("123456", "Bluetooth is currently powered off")
                }
            }

            CreekManager.sInstance.phoneBookInit()

            CreekManager.sInstance.callStatusUpdate { model: Call.protocol_call_remind_status ->
                Log.w("123456", model.toString())
                if (model.status == Enums.call_status.RECEIVED_CALL){
                    ///Watch notification app rejects incoming call
                }
            }
            CreekManager.sInstance.messageReplyListen { model: Message.protocol_message_reply_send_operate ->
                if (model.replyType == Enums.msg_reply_type.MSG_REPLY_CALL){
                    var phone = model.msgId.toStringUtf8().substring(1,model.msgId.count())
                    var slotId = model.msgId.toStringUtf8().substring(0,1)
                    sendSms(number = phone, message = model.sendContent.toStringUtf8(), slotId = slotId.toInt())
                }else{
                    MyNotificationListenerService.getInstance()
                        ?.sendReply(key = model.msgId.toStringUtf8(), replyMessage = model.sendContent.toStringUtf8())
                }

            }
            CreekManager.sInstance.watchResetListen {
                Log.w("watchResetListen", "The watch is in reset state")
                CreekManager.sInstance.bindingDevice(Enums.bind_method.BIND_NORMAL, id = null,code = null,success = {

                }, failure = {

                })
            }
            val keyId = "*********"
            val publicKey = "***********"
            
            CreekManager.sInstance.aiVoiceConfig(keyId = keyId, publicKey = publicKey)

            CreekManager.sInstance.ephemerisInit(keyId = keyId, publicKey = publicKey, model = {
                return@ephemerisInit EphemerisGPSModel(
                    isVaild = true,
                    altitude = 10,
                    latitude = (22.312653 * 1000000).toInt(),
                    longitude = (114.027986 * 1000000).toInt()
                )
            })

        })

        setContent {
            Creek_dial_androidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }


         fun requestBluetoothPermissions() {
            val permissions = arrayOf(
                // Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_MEDIA_LOCATION

            )

            ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSION)
        }

         fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 用户授予了蓝牙权限，可以执行相关操作

                } else {
                    // 用户拒绝了蓝牙权限，可以显示一条提示信息或执行其他操作
                    //  Toast.makeText(this, "拒绝了蓝牙权限所以不能扫描设备", Toast.LENGTH_SHORT).show()
                }
            }
        }

         fun hasBluetoothPermissions(): Boolean {
            //compileSdkVersion项目中编译SDK版本大于30申请以下权限可使用
            //Manifest.permission.BLUETOOTH_SCAN、Manifest.permission.BLUETOOTH_ADVERTISE、Manifest.permission.BLUETOOTH_CONNECT
            //若小于30可以直接使用权限对应的字符串
            if (Build.VERSION.SDK_INT > 30) {
                if ((ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.BLUETOOTH_SCAN"
                    ) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this, "android.permission.BLUETOOTH_ADVERTISE"
                    )
                            != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this, "android.permission.ACCESS_COARSE_LOCATION"
                    )
                            != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.BLUETOOTH_CONNECT"
                    ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            "android.permission.BLUETOOTH_SCAN",
                            "android.permission.BLUETOOTH_ADVERTISE",
                            "android.permission.BLUETOOTH_CONNECT"
                        ), REQUEST_BLUETOOTH_PERMISSION
                    )
                    return false
                }
            }
            return true
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun sendSms(number: String?, message: String?, slotId:Int?) {
        val smsSelfPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        )
        if(smsSelfPermission == PackageManager.PERMISSION_GRANTED){
            if (number != null && message != null && slotId != null) {
                val subscriptionManager =
                    this!!.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                val subscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
                var foundSubscription = false
                for (subscriptionInfo in subscriptionInfoList) {
                    val simSlotIndex = subscriptionInfo.simSlotIndex
                    if (simSlotIndex == slotId){
                        val subscriptionId = subscriptionInfo.subscriptionId
                        val smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId)
                        smsManager.sendTextMessage(number, null, message, null, null)
                        Log.d("SMS", "Message sent from SIM slot: $slotId")
                        break
                    }
                }
                if (!foundSubscription) {
                    // If the specified card slot is not found, use the default SmsManager to send the SMS
                    val defaultSmsManager = SmsManager.getDefault()
                    defaultSmsManager.sendTextMessage(number, null, message, null, null)
                    Log.d("SMS", "Default SMS Manager used to send message")
                }
            }
        }

    }
}





@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(bottomBar = { BottomBar(navController) }) { innerPadding ->
        NavHost(
            startDestination = "home",
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        ) {

            navigation(route = "home", startDestination = SdkFunction.route) {
                homeGraph(navController)
            }

            composable(
                "customDial/{titleName}/{width}/{height}/{cornerRadius}",
                arguments = listOf(
                    navArgument("titleName") { type = NavType.StringType },
                    navArgument("width") { type = NavType.IntType },
                    navArgument("height") { type = NavType.IntType },
                    navArgument("cornerRadius") { type = NavType.IntType }
                )
            ) {backStackEntry ->
                val titleName = backStackEntry.arguments?.getString("titleName") ?: ""
                val width = backStackEntry.arguments?.getInt("width") ?: 0
                val height = backStackEntry.arguments?.getInt("height") ?: 0
                val cornerRadius = backStackEntry.arguments?.getInt("cornerRadius") ?: 0
                CustomDialScreen(navController,titleName, width = width, height = height, cornerRadius = cornerRadius)
            }

            composable(
                "sendCommand/{functionStr}",
                arguments = listOf(navArgument("functionStr") { type = NavType.StringType })
            ) { backStackEntry ->
                val functionStr = backStackEntry.arguments?.getString("functionStr") ?: ""
                SendCommandScreen(navController, functionStr)
            }

            composable("scanDevice",) {
                ScanDeviceScreen(navController)
            }

            composable("music",) {
                MusicUploadScreen(navController)
            }
        }
    }
}


@Composable
fun BottomBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentRoute = currentDestination?.route ?: ""

    val routes = remember { tabRowScreens.map { it.route } }





    if (currentRoute in routes) {
        BottomNavigation(backgroundColor = Color.White) {
            tabRowScreens.forEach { screen ->
                val selected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true
                BottomNavigationItem(
                    selected = selected,
                    icon = {
                        Icon(
                            screen.icon!!,
                            contentDescription = null,
                            tint = if (selected) Color.Blue else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = screen.route,
                            style = TextStyle(color = if (selected) Color.Blue else Color.Gray)
                        )
                    },
                    onClick = {
                        try {
                            navController.navigate(screen.route) {
                                restoreState = true
                                launchSingleTop = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    })
            }
        }
    }
}





@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
fun NavGraphBuilder.homeGraph(navController: NavHostController) {



    composable(SdkFunction.route) {

        val cameraPermissionState  =  rememberMultiplePermissionsState(permissions = arrayOf(
            // Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        ).toList()
        )

        SdkFunction(
            chooseFunction = { functionStr ->
                if (functionStr == "Upload Music"){
                    navController.navigate("music")
                }else{
                    navController.navigate("sendCommand/$functionStr")
                }

//                WeatherCommand().setWeather()


            },
            scanDevice = {

                navController.navigate("scanDevice")
                if (cameraPermissionState.allPermissionsGranted){
                    navController.navigate("scanDevice")
                }else{
                    cameraPermissionState.launchMultiplePermissionRequest()
                }


            }
        )

    }
    composable(Dial.route) {
        DialScreen(navController)
    }


}




