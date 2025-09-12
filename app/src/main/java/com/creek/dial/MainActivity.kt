package com.creek.dial

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.arthenica.ffmpegkit.FFmpegKit
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
import com.example.mylibrary.VoiceDialType
import com.example.mylibrary.eventIdType
import com.example.proto.Call
import com.example.proto.Enums
import com.example.proto.Message
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.notification_listener_util.music.CreekMediaControllerUtils
import kotlinx.coroutines.delay
import okio.Timeout
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask
import java.util.logging.Handler
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
            val keyId = "*************"
            val publicKey = "********************"
            CreekManager.sInstance.initGlobalConfig(keyId, publicKey)
            CreekManager.sInstance.ephemerisListen {
              val model =  EphemerisGPSModel(
                    isVaild = true,
                    altitude = 10,
                    latitude = (22.312653 * 1000000).toInt(),
                    longitude = (114.027986 * 1000000).toInt()
                )
                CreekManager.sInstance.updateEphemeris(model = model, success = {
                    Log.w("123456", "updateEphemeris success")
                }, failure = {
                    c,m ->
                    Log.w("123456", "updateEphemeris failure")
                })
            }
            CreekManager.sInstance.listenDeviceState { status, deviceName ->
                Log.w("123456", "$status++++$deviceName")
            }
//            CreekManager.sInstance.externalConnect(id = "F4:4E:FD:64:0B:44", connect = {
//                CreekManager.sInstance.getAuthorizationCode(model = {
//                    str ->
//                    CreekManager.sInstance.saveBindDevice()
//                }, failure = {
//                    c,m ->
//                })
//
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

            CreekManager.sInstance.aiVoiceConfig(keyId = keyId, publicKey = publicKey)
            CreekManager.sInstance.setAiVoiceCountry(countryCode = "US")
            CreekManager.sInstance.setAiVoiceCity(cityName = "shengzhen")


            CreekManager.sInstance.liveSportDataListen { model ->
                println(model.toString())
            }
            CreekManager.sInstance.liveSportControlListen { model ->
                println(model.toString())
            }

            CreekManager.sInstance.aiDialConfig(
                voiceData = {
                    pcmData ->


                    // 1. 先把 pcmData 写入临时文件
//                    val pcmFile = File(this.cacheDir, "${System.currentTimeMillis()}.pcm")
//                    pcmFile.writeBytes(pcmData)
//
//                    // 2. 输出文件路径
//                    val wavFile = File(this.cacheDir, "${System.currentTimeMillis()}.wav")
//                    val wavFilePath = wavFile.absolutePath
//
//                    // 3. FFmpeg 命令，输入直接用 pcm 文件路径
//                    val command = "-f s16le -ar 16000 -ac 1 -i ${pcmFile.absolutePath} $wavFilePath"
//                    // 4. 异步执行转换
//                    FFmpegKit.executeAsync(command) { session ->
//                        val returnCode = session.returnCode
//                        if (returnCode.isValueSuccess) {
//                            Log.d("FFmpeg", "PCM converted to WAV successfully, file path: $wavFilePath")
//
//                        } else {
//                            Log.e("FFmpeg", "PCM to WAV conversion failed, error: ${session.failStackTrace}")
//
//                        }
//                        // 临时文件删除
//                        pcmFile.delete()
//                    }

                    ///pcm 音频数据（date）
                    ///转文本
                    ///文本转成功之后
                    CreekManager.sInstance.aiDialSendText("我想生成一个小狗", type = VoiceDialType.normal)
//                    ///识别错误
//                    CreekManager.sInstance.aiDialSendText("我想生成一个小狗", type = VoiceDialType.error)
//                    /// 网络错误
//                    CreekManager.sInstance.aiDialSendText("我想生成一个小狗", type = VoiceDialType.networkError)

                },
                confirmText = {
                    text ->
                    println(text)
                    //生成Ai图片
                    ///生成完成之后 下发图片数据
                    val bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.fun061101_03)
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    val imageData = outputStream.toByteArray()
                    val images: List<ByteArray> = listOf(imageData)
                    CreekManager.sInstance.aiDialSendImages(images = images, type = VoiceDialType.normal, dialName = "twoDial");

                }, success = {
                    println("dial success")

                }, failure = {
                    c,m ->
                    println("dial $m")
                }
            )

//            CreekManager.sInstance.setSportControl(
//                controlType = Enums.exercise_control_type.CONTROL_RESUME, success = {
//                    println("setSportControl success")
//                }, failure = {
//                     c,m ->
//                    println("setSportControl failure")
//                }
//            )



            CreekManager.sInstance.calendarConfig(timerMinute = 10, systemCalendarName = "CREEK", isSupport = true, model = {
                msg ->
                Log.w("calendarConfig", msg)
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
                    this.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                val subscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
                val foundSubscription = false
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

            // 添加 videoDial 路由
            composable(
                "videoDial/{titleName}/{width}/{height}/{cornerRadius}",
                arguments = listOf(
                    navArgument("titleName") { type = NavType.StringType },
                    navArgument("width") { type = NavType.IntType },
                    navArgument("height") { type = NavType.IntType },
                    navArgument("cornerRadius") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val titleName = backStackEntry.arguments?.getString("titleName") ?: ""
                val width = backStackEntry.arguments?.getInt("width") ?: 0
                val height = backStackEntry.arguments?.getInt("height") ?: 0
                val cornerRadius = backStackEntry.arguments?.getInt("cornerRadius") ?: 0
                com.creek.dial.videoDial.VideoDialScreen(
                    navController = navController,
                    titleName = titleName,
                    width = width,
                    height = height,
                    cornerRadius = cornerRadius
                )
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

fun NavController.navigateToVideoDialScreen(
    titleName: String,
    width: Int,
    height: Int,
    cornerRadius: Int
) {
    this.navigate("videoDial/$titleName/$width/$height/$cornerRadius")
}




