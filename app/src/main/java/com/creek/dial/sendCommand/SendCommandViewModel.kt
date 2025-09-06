package com.creek.dial.sendCommand

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.example.model.ActivityModel
import com.example.model.BaseDataModel
import com.example.model.BaseModel
import com.example.model.EphemerisModel
import com.example.model.HeartRateModel
import com.example.model.NoiseModel
import com.example.model.OxygenModel
import com.example.model.PhoneModel
import com.example.model.ScanDeviceModel
import com.example.model.SleepModel
import com.example.model.SportModel
import com.example.model.StressModel
import com.example.model.UpgradeModel
import com.example.model.fromTable
import com.example.mylibrary.CreekManager
import com.example.mylibrary.SportTypeSerializer
import com.example.mylibrary.sportType
import com.example.proto.Alarm
import com.example.proto.AppList
import com.example.proto.Call
import com.example.proto.Card
import com.example.proto.Contacts
import com.example.proto.Deviceinfo
import com.example.proto.Disturb
import com.example.proto.Enums
import com.example.proto.Findphone
import com.example.proto.Focus
import com.example.proto.Language
import com.example.proto.Message
import com.example.proto.Monitor
import com.example.proto.Mtu
import com.example.proto.Screen
import com.example.proto.SleepMonitor
import com.example.proto.Sport
import com.example.proto.Standing
import com.example.proto.Table
import com.example.proto.Time
import com.example.proto.Userinfo
import com.example.proto.Watchdial
import com.example.proto.WaterMonitor
import com.example.proto.Weather
import com.example.proto.Wordtime
import com.google.gson.GsonBuilder
import com.google.protobuf.ByteString
import java.io.File
import java.io.IOException
import java.time.Instant
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.model.ContactsIconModel
import com.example.model.ContactsTable
import com.example.model.CourseModel
import com.example.model.DialPhotoParseModel
import com.example.model.EphemerisGPSModel
import com.example.mylibrary.CancelAutoConnectType
import com.example.mylibrary.SyncServerType
import com.example.proto.Calendar
import com.example.proto.Geo
import com.example.proto.Morning
import com.example.proto.Music
import com.example.proto.WatchSensor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.FileInputStream
import java.io.InputStream

class SendCommandViewModel {

    val responseText = mutableStateOf("")

    var loddingState = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendCommand(title:String, context:Context) {
//        loddingState.value = true
        when (title) {
            "Binding" -> {

                CreekManager.sInstance.bindingDevice(
                    bindType = Enums.bind_method.BIND_NORMAL,
                    id = null,
                    code = null,
                    success = {
                        responseText.value = "Binding success"
//                        loddingState.value = false
                    },
                    failure = {
                        responseText.value = "Binding failure"
//                        loddingState.value = false

                    })

            }
            "Sync" -> {
                loddingState.value = false
                CreekManager.sInstance.sync(syncSuccess = {
                    responseText.value = "Success"
                }, syncFailure = {
                    responseText.value = "Failure"
                }, syncProgress = { progress: Int ->
                    responseText.value = "progress :$progress"
                })

            }
            "Upload" -> {
                try {
                    val inputStream = context.assets.open("ewr01_noise_fw_ota_2025060522_v0.0.11_no_music.ota")
                    // 使用 inputStream 处理资源数据
                    var fileData: ByteArray = inputStream.readBytes()
                    val decimalArray = IntArray(fileData.size)
                    for (i in fileData.indices) {
                        decimalArray[i] = fileData[i].toInt() and 0xFF
                    }
//                    loddingState.value = false
                    CreekManager.sInstance.upload(
                        "ewr01_noise_fw_ota_2025060522_v0.0.11_no_music.ota",
                        decimalArray,
                        uploadProgress = { progress ->
                            responseText.value = "progress :$progress"
                        },
                        uploadSuccess = {
                            responseText.value = "Success"
                        },
                        uploadFailure = { c, m ->
                            responseText.value = "Failure"

                        })
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
//
//                val filePath = "/data/user/0/com.creek.dial/app_flutter/creek/ewr01_noise_fw_ota_2025060522_v0.0.11_no_music.ota"
//                CreekManager.sInstance.uploadWithFilePath(
//                    fileName = "ewr01_noise_fw_ota_2025060522_v0.0.11_no_music.ota",
//                    filePath = "/data/user/0/com.creek.dial/app_flutter/creek/ewr01_noise_fw_ota_2025060522_v0.0.11_no_music.ota",
//                    uploadProgress = { progress ->
//                        responseText.value = "progress :$progress"
//                    },
//                    uploadSuccess = {
//                        responseText.value = "Success"
//                    },
//                    uploadFailure = { c, m ->
//                        responseText.value = "Failure"
//
//                    })

            }
            "Upload(zip)" -> {
                CreekManager.sInstance.getOTAUpgradeVersion {
                    Log.w("OTA", "OTA version:$it")
                }
                try {
                    val inputStream = context.assets.open("titan.zip")
                    // 使用 inputStream 处理资源数据
                    var fileData: ByteArray = inputStream.readBytes()
                    val decimalArray: IntArray =
                        fileData.map { it.toInt() and 0xFF }.toIntArray()

                    CreekManager.sInstance.getOTAUpgradeState(fileName = "titan.zip", fileData = decimalArray, model = {
                        model: UpgradeModel ->
                        Log.w("ota", "totalResource:${model.totalResource ?: 0}  step:${model.step ?: 0}")
                        CreekManager.sInstance.upload(
                            "titan.zip",
                            decimalArray,
                            uploadProgress = { progress ->
                                responseText.value = "progress :$progress"
                            },
                            uploadSuccess = {
                                responseText.value = "Success"
                            },
                            uploadFailure = { c, m ->
                                responseText.value = "Failure"

                            })
                    } , failureArgument = {
                        code, message ->
                        Log.w("ota", "$message")
                    })


                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            "phone book" -> {
                var phone = PhoneModel(
                    "bean",
                    "13420902893"
                )
                var phone2 = PhoneModel(
                    "bean2",
                    "13420902898"
                )

                CreekManager.sInstance.encodePhoneFile(listOf(phone,phone2), model = {data: ByteArray ->
                    val decimalArray: IntArray =
                        data.map { it.toInt() and 0xFF }.toIntArray()
                    CreekManager.sInstance.backstageUpload(
                        "creek.phone",
                        decimalArray,
                        uploadProgress = { progress ->
                            responseText.value = "progress :$progress"
                        },
                        uploadSuccess = {
                            responseText.value = "Success"
                        },
                        uploadFailure = { c, m ->
                            responseText.value = "Failure"
                        })
                }, failure = {code: Int, message: String ->

                })

            }
            "Off-line ephemeris" -> {
                try {
                    val inputStream =  context.assets.open("offlineEphemeris.agnss")
                    // 使用 inputStream 处理资源数据
                    var fileData: ByteArray = inputStream.readBytes()
                    val filePath = "/data/user/0/com.example.creeksdkdemo/app_flutter/creek/offlineEphemeris.agnss"
                    var ephemerisModel = EphemerisModel(
                        socName = "",
                        startUtcTime = Instant.now().epochSecond.toInt() ,
                        endUtcTime = Instant.now().epochSecond.toInt(),
                        isVaild = true,
                        latitude = 22374765,
                        latitudeDire = "N",
                        longitude = 114019333,
                        longitudeDire = "E",
                        altitude = 10,
                        filePath =  filePath,
                        fileSize = fileData.size
                    )
                    CreekManager.sInstance.encodeOfflineFile(ephemerisModel = ephemerisModel, model = { data ->
                        val decimalArray: IntArray =
                            data.map { it.toInt() and 0xFF }.toIntArray()
                        CreekManager.sInstance.backstageUpload(
                            "offlineEphemeris.agnss",
                            decimalArray,
                            uploadProgress = { progress ->
                                responseText.value = "progress :$progress"
                            },
                            uploadSuccess = {
                                responseText.value = "Success"
                            },
                            uploadFailure = { c, m ->
                                responseText.value = "Failure"
                            })
                    }, failure = {c,m ->

                    })



                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            "pair" -> {
                CreekManager.sInstance.androidPair()
            }
            "Get Device Information" -> {
                CreekManager.sInstance.getFirmware({ model: Deviceinfo.protocol_device_info ->

                    responseText.value = model.toString()
                    CreekManager.sInstance.getSNFirmware(model,{
                        Log.w("sn", "sn++++$it")
                    })

                }, failure = { c, m ->
                    responseText.value = m
                })

            }
            "Get Device Bluetooth Status" -> {

                CreekManager.sInstance.bluetoothStatus({ model: Mtu.protocol_connect_status_inquire_reply ->

                    responseText.value = model.toString()

                }, failure = { _, m ->

                    responseText.value = m
                })

            }
            "Sync Time" -> {
                CreekManager.sInstance.syncTime(success = {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Get Time" -> {
                CreekManager.sInstance.getTime({ model: Time.protocol_device_time_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Get User Information" -> {
                CreekManager.sInstance.getUserInfo({ model: Userinfo.protocol_user_info_operate ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Set User Information" -> {
                CreekManager.sInstance.getUserInfo({ model: Userinfo.protocol_user_info_operate ->
                    model.personalInfo.year = 2024
                    model.personalInfo.month = 11
                    model.goalSetting.workoutDay = 7
                    model.goalSetting.steps = 100
                    model.goalSetting.notifyFlag = Enums.notify_type.CLOSE
                    CreekManager.sInstance.setUserInfo(model = model, {
                        responseText.value = "success"
                    }, failure = { c, m ->
                        responseText.value = m

                    })

                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Get Alarm Clock" -> {
                CreekManager.sInstance.getAlarm({ model: Alarm.protocol_alarm_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Set Alarm Clock" -> {
                CreekManager.sInstance.getAlarm({ model: Alarm.protocol_alarm_inquire_reply ->
                    var data = Alarm.protocol_alarm_operate()
                    data.addAllAlarmItem(model.alarmItemList)
                    var item = Alarm.protocol_set_alarm_item()
                    item.alarmId = 1;
                    item.dispStatus = Enums.disp_status.DISP_ON
                    item.type = Enums.alarm_type.GET_UP
                    item.hour = 22
                    item.minute = 30
                    item.addAllRepeat(listOf(true, true, true, true, true, false, false))
                    item.switchFlag = false
                    item.laterRemindRepeatTimes = 1
                    item.vibrateOnOff = true
                    item.name = ByteString.copyFrom("abc".toByteArray())
                    if(model.fromTable().later_remind_min){
                        item.laterRemindMin = 1

                    }
                    data.addAlarmItem(item)
                    if(model.fromTable().custom_name_list){
                        data.addCustomNameList(ByteString.copyFrom("hello".toByteArray()))
                        data.addCustomNameList(ByteString.copyFrom("hello2".toByteArray()))
                    }

                    CreekManager.sInstance.setAlarm(model = data, {
                        responseText.value = "success"
                    }, failure = { c, m ->
                        responseText.value = m

                    })

                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Get Do Not Disturb" -> {
                CreekManager.sInstance.getDisturb({ model: Disturb.protocol_disturb_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Set Do Not Disturb" -> {
                var model = Disturb.protocol_disturb_operate()
                model.disturbOnOff = true
                CreekManager.sInstance.setDisturb(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Get Screen Brightness" -> {
                CreekManager.sInstance.getScreen({ model: Screen.protocol_screen_brightness_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Set Screen Brightness" -> {

                CreekManager.sInstance.getScreen({ model: Screen.protocol_screen_brightness_inquire_reply ->

                    var operate = Screen.protocol_screen_brightness_operate()
                    var screenTable = model.fromTable()
                    if(screenTable.steady){
                        var aod = Screen.protocol_screen_aod_time_setting()
                        aod.mode = Enums.aod_mode.INTELLIGENT_MODE
                        aod.startHour = 8
                        aod.startMinute = 0
                        aod.endHour = 10
                        aod.endMinute = 0
                        operate.aodTimeSetting = aod
                    }else{
                        operate.aodSwitchFlag = true
                    }
                    operate.level = 100
                    operate.showInterval = 5
                    operate.levelFlag = true
                    CreekManager.sInstance.setScreen(model = operate, {
                        responseText.value = "success"
                    }, failure = { _, m ->
                        responseText.value = m
                    })
                }, failure = { _, m ->
                    responseText.value = m
                })


            }
            "Get Health Monitoring" -> {
                var model = Monitor.protocol_health_monitor_operate()
                model.healthType = Enums.health_type.HEART_RATE

                CreekManager.sInstance.getMonitor(
                    operate = model,
                    { model: Monitor.protocol_health_monitor_inquire_reply ->
                        responseText.value = model.toString()
                    },
                    failure = { _, m ->
                        responseText.value = m
                    })

            }
            "Health monitoring setting" -> {
                var model = Monitor.protocol_health_monitor_operate()
                model.healthType = Enums.health_type.HEART_RATE
                CreekManager.sInstance.setMonitor(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Sleep monitoring acquisition" -> {
                CreekManager.sInstance.getSleepMonitor({ model: SleepMonitor.protocol_sleep_monitor_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Sleep monitoring setting" -> {
                var model = SleepMonitor.protocol_sleep_monitor_operate()
                model.switchFlag = true
                CreekManager.sInstance.setSleepMonitor(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "World clock setting" -> {
                var model = Wordtime.protocol_world_time_operate()
                var item = Wordtime.protocol_world_time_item()
                item.cityName = ByteString.copyFrom("shenzheng".toByteArray())
                item.offestMin = 120
                item.customMin = - 180
                model.addWorldTimeItem(item)
                CreekManager.sInstance.setWorldTime(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "World clock acquisition" -> {
                CreekManager.sInstance.getWorldTime({ model: Wordtime.protocol_world_time_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Message switch query" -> {
                CreekManager.sInstance.getMessageOnOff({ model: Message.protocol_message_notify_switch_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Message switch setting" -> {
                var model = Message.protocol_message_notify_switch()
                model.notifySwitch = true
                CreekManager.sInstance.setMessageOnOff(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Message content setting" -> {
                var model = Message.protocol_message_notify_data()
                model.osPlatform = Enums.notify_os_platform.ANDROID_NOTIFY
                model.notifyFlag = Enums.notify_type.ALLOW
                model.remindType = Enums.message_remind_type.Wechat
                model.contactText = ByteString.copyFrom("bean".toByteArray())
                model.msgContent =  ByteString.copyFrom("hello".toByteArray())
                CreekManager.sInstance.setMessageApp(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }

            "getMessageReply" -> {

                CreekManager.sInstance.getMessageReply({
                    model: Message.protocol_message_reply_list_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = {
                    c,m ->
                    responseText.value = m
                })
            }
            "setMessageReply" -> {
                var model = Message.protocol_message_reply_list_operate()
                model.addCallingReplyItems(ByteString.copyFrom("hello11111".toByteArray()))
                model.addMsgReplyItems(ByteString.copyFrom("hello22222".toByteArray()))
                CreekManager.sInstance.setMessageReply(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }

            "Set weather" -> {
                var model = Weather.protocol_weather_operate()
                model.switchFlag = true
                var item = Weather.protocol_weather_detail_data_item()
                item.hour = 14
                item.curTemp = 30
                item.curMaxTemp = 33
                item.curMinTemp = 26
                item.visibilityLevel = ByteString.copyFrom("hello".toByteArray())
                item.atmosHpa = (1015.1*100).toInt()
                model.addDetailDataItem(item)
                CreekManager.sInstance.setWeather(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Incoming call configuration query" -> {
                CreekManager.sInstance.getCall({ model: Call.protocol_call_switch_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Incoming call configuration settings" -> {
                var model = Call.protocol_call_switch()
                model.callSwitch = true
                model.callDelay = 5
                CreekManager.sInstance.setCall(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Contacts query" -> {
                CreekManager.sInstance.getContacts({ model: Contacts.protocol_frequent_contacts_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Contacts settings" -> {

                CreekManager.sInstance.getContacts({ model: Contacts.protocol_frequent_contacts_inquire_reply ->
                    var table = model.fromTable()
                    if(table.contact_icon){
                        val list = mutableListOf<ContactsIconModel>()
                        val filePath = "/data/user/0/com.creek.dial/app_flutter/creek/1/firmware/background/background_0_0565.png"

                        for (i in 0 until 10) {
                            var data = ContactsIconModel(phoneNum = "1234567891${i}", path = filePath, w = model.contactIconWidth, h = model.contactIconHeight, quality = if (table.contact_icon_lz4) 10 else 100,isLz4 = if (table.contact_icon_lz4) 1 else 0)
                            list.add(data)
                        }

                        CreekManager.sInstance.encodeContacts(contactsIconModels = list, model = {
                                data ->
                            val decimalArray: IntArray =
                                data.map { it.toInt() and 0xFF }.toIntArray()
                            CreekManager.sInstance.upload(fileName = "icon.contact_icon", fileData = decimalArray,uploadProgress = {
                                    progress: Int ->

                            }, uploadSuccess = {
                                var model = Contacts.protocol_frequent_contacts_operate()
                                for (i in 0 until 10) {
                                    var item = Contacts.protocol_frequent_contacts_item()
                                    item.phoneNumber = ByteString.copyFrom("1234567891${i}".toByteArray())
                                    item.contactName = ByteString.copyFrom("bean${i}".toByteArray())
                                    model.addContactsItem(item)
                                }
                                CreekManager.sInstance.setContacts(model = model, {
                                    responseText.value = "success"
                                }, failure = { _, m ->
                                    responseText.value = m
                                })
                            }, uploadFailure = {
                                    code: Int, message: String ->
                                responseText.value = message
                            })

                        }, failure = {
                            code: Int, message: String ->
                        })

                    }else{
                        var model = Contacts.protocol_frequent_contacts_operate()
                        var item = Contacts.protocol_frequent_contacts_item()
                        item.phoneNumber = ByteString.copyFrom("12345678912".toByteArray())
                        item.contactName = ByteString.copyFrom("bean".toByteArray())
                        model.addContactsItem(item)
                        CreekManager.sInstance.setContacts(model = model, {
                            responseText.value = "success"
                        }, failure = { _, m ->
                            responseText.value = m
                        })
                    }
                }, failure = { _, m ->
                    responseText.value = m
                })



            }
            "Exercise self-identification query" -> {
                CreekManager.sInstance.getSportIdentification({ model: Sport.protocol_exercise_intelligent_recognition_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Exercise self-identification settings" -> {
//                var model = Sport.protocol_exercise_intelligent_recognition()
//                model. = true
//                CreekManager.sInstance.setSportIdentification(model = model, {
//                    responseText.value = "success"
//                }, failure = { _, m ->
//                    responseText.value = m
//                })
            }
            "Exercise sub-item data query" -> {
                CreekManager.sInstance.getSportSub({ model: Sport.protocol_exercise_sporting_param_sort_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Exercise sub-item data setting" -> {
                var model = Sport.protocol_exercise_sporting_param_sort()
                model.sportType = Enums.sport_type.BARBELL.value
                CreekManager.sInstance.setSportSub(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Inquiry about the arrangement order of device exercise" -> {
                CreekManager.sInstance.getSportSort({ model: Sport.protocol_exercise_sport_mode_sort_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Setting the arrangement order of device exercise" -> {
                var model = Sport.protocol_exercise_sport_mode_sort()
                model.addSportItems(Enums.sport_type.BADMINTON)
                CreekManager.sInstance.setSportSort(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Get the type of exercise supported by the device" -> {
                CreekManager.sInstance.getSportType({ model: Sport.protocol_exercise_func_support_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Setting the heart rate interval" -> {
                var model = Sport.protocol_exercise_heart_rate_zone()
                model.zone1 = 100
                model.zone2 = 100
                model.zone3 = 100
                model.zone4 = 100
                model.zone5 = 100
                model.zone6 = 100

                CreekManager.sInstance.setSportHeartRate(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Delete the dial" -> {
                var model = Watchdial.protocol_watch_dial_plate_operate()
                model.addDialName(ByteString.copyFrom("1".toByteArray()))
                CreekManager.sInstance.delWatchDial(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Set the dial" -> {
                var model = Watchdial.protocol_watch_dial_plate_operate()
                model.addDialName(ByteString.copyFrom("1".toByteArray()))
                CreekManager.sInstance.setWatchDial(model = model, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Query the dial" -> {
                CreekManager.sInstance.getWatchDial({ model: Watchdial.protocol_watch_dial_plate_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Set Language" -> {
                CreekManager.sInstance.setLanguage(type = Enums.language.CHINESE, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Get Language" -> {
                CreekManager.sInstance.getLanguage({ model: Language.protocol_language_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "System operation" -> {
                CreekManager.sInstance.setSystem(type = 1, {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Query activity data" -> {
                CreekManager.sInstance.getActivityNewTimeData(
                    startTime = "2023-10-01",
                    endTime = "2024-11-23"
                ) { model: BaseModel<List<ActivityModel>> ->

                    responseText.value = model.data?.toList().toString()

                }

//

            }
            "Query sleep data" -> {
                CreekManager.sInstance.getSleepNewTimeData(
                    startTime = "2023-08-01",
                    endTime = "2023-09-01"
                ) { model: BaseModel<List<SleepModel>> ->
                    responseText.value = model.data?.toList().toString()

                }

            }
            "Query heart rate data" -> {
                CreekManager.sInstance.getHeartRateNewTimeData(
                    startTime = "2023-08-01",
                    endTime = "2023-09-01"
                ) { model: BaseModel<List<HeartRateModel>> ->
                    responseText.value = model.data?.toList().toString()

                }

            }
            "Query pressure data" -> {
                CreekManager.sInstance.getStressNewTimeData(
                    startTime = "2023-08-01",
                    endTime = "2023-09-01"
                ) { model: BaseModel<List<StressModel>> ->
                    responseText.value = model.data?.toList().toString()

                }

            }
            "Query noise data" -> {
                CreekManager.sInstance.getNoiseNewTimeData(
                    startTime = "2023-08-01",
                    endTime = "2023-09-01"
                ) { model: BaseModel<List<NoiseModel>> ->
                    responseText.value = model.data?.toList().toString()

                }
            }

            "Query blood oxygen data" -> {
                CreekManager.sInstance.getSpoNewTimeData(
                    startTime = "2024-11-20",
                    endTime = "2024-11-20"
                ) { model: BaseModel<List<OxygenModel>> ->
                    responseText.value = model.data?.toList().toString()

                }

            }
            "Exercise record list" -> {
                CreekManager.sInstance.getSportRecord(type = null) { model: BaseModel<List<SportModel>> ->
                    responseText.value = model.data?.toList().toString()
                }

            }
            "Query exercise details" -> {
                CreekManager.sInstance.getSportDetails(id = 1) { model: BaseModel<SportModel> ->
                    responseText.value = model.data?.toString().toString()

                }
               var  operate = Findphone.protocol_find_phone_watch_operate()
                operate.findWatchFlag = true
                operate.findWatchSwitch = true


                CreekManager.sInstance.setFindPhoneWatch(model = operate, success = {
                    responseText.value = "success"
                }, failure = { _, m ->
                    responseText.value = m
                })

            }
            "Range query exercise record" -> {
                CreekManager.sInstance.getSportTimeData(
                    startTime = "2025-09-06",
                    endTime = "2025-09-06",
                    type = null
                ) { model: BaseModel<List<SportModel>> ->
                    val gson = GsonBuilder()
                        .registerTypeAdapter(sportType::class.java, SportTypeSerializer())
                        .create()
                    responseText.value = gson.toJson(model)

                }

            }
            "Delete exercise record" -> {
                CreekManager.sInstance.delSportRecord(id = 1) { model: BaseModel<BaseDataModel> ->
                    responseText.value = model.toString()

                }
            }
            "getLogPath" -> {
                CreekManager.sInstance.getLogPath {
                    val file = File(it)
                    if (file.exists()) {
                        val authority = "${context.packageName}.provider"
                        val uri = FileProvider.getUriForFile(context, authority, file)
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "application/zip" // 根据你的文件类型来设置
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        val chooserIntent = Intent.createChooser(intent, "Share file")
                        startActivity(context,chooserIntent,null)

                    }
                }

            }
            "Get card" -> {
                CreekManager.sInstance.getCard({model: Card.protocol_quick_card_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "Set card" -> {
                CreekManager.sInstance.getCard({model: Card.protocol_quick_card_inquire_reply ->
                    var operate = Card. protocol_quick_card_operate()
                    val cardType: MutableList<Enums.quick_card_type> = mutableListOf()
                    model.cardTypeList.forEach { quickCardType ->
                        if (quickCardType == Enums.quick_card_type.CARD_TYPE_DIAL) {
                            if (!model.cardTypeDialSupport.isDelete) {
                                ///Removal is not supported and must be added
                                cardType.add(quickCardType)
                            }
                        }else{
                            if(quickCardType == Enums.quick_card_type.CARD_TYPE_ACTIVITY){

                            }else{
                                cardType.add(quickCardType)
                            }
                        }
                    }
                    operate.addAllCardType(cardType)
                    CreekManager.sInstance.setCard(model = operate, success = {
                        responseText.value = "success"
                    }, failure = {_, m ->
                        responseText.value = m
                    })
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "getStand" -> {
                CreekManager.sInstance.getStanding({model: Standing.protocol_standing_remind_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "setStand" -> {
                var  operate =  Standing.protocol_standing_remind_operate()
                var standing =  Standing.protocol_standing_remind_set()
                ///Just set the switch  other attributes do not need to be set
                standing.switchFlag = true
                operate.standingRemind = standing
                CreekManager.sInstance.setStanding(model = operate, success = {
                    responseText.value = "success"
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "getWater" -> {
                CreekManager.sInstance.getWater({model: WaterMonitor.protocol_drink_water_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "setWater" -> {
                var  operate =  WaterMonitor.protocol_drink_water_operate()
                operate.switchFlag = true
                operate.startHour = 8
                operate.startMinute = 0
                operate.endHour = 18
                operate.endMinute = 0
                operate.addAllRepeat(listOf(true,true,true,true,true,true,true))
                CreekManager.sInstance.setWater(model = operate, success = {
                    responseText.value = "success"
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "getFocus" -> {
                CreekManager.sInstance.getFocusSleep({model: Focus.protocol_focus_mode_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "setFocus" -> {
                var  operate =  Focus.protocol_focus_mode_operate()
                var mode = Focus.protocol_focus_sleep_mode()
                mode.switchFlag = true
                mode.startHour = 22
                mode.endHour = 8
                mode.startMinute = 0
                mode.endMinute = 0
                operate.sleepMode = mode
                CreekManager.sInstance.setFocusSleep(model = operate, success = {
                    responseText.value = "success"
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "getAppList" -> {
                CreekManager.sInstance.getAppList({model: AppList.protocol_app_list_inquire_reply ->
                    responseText.value = model.toString()
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "setAppList" -> {
                var  operate =  AppList.protocol_app_list_operate()
                CreekManager.sInstance.setAppList(model = operate, success = {
                    responseText.value = "success"
                }, failure = {_, m ->
                    responseText.value = m
                })

            }
            "functionTable" -> {
                CreekManager.sInstance.getTable({model: Table.protocol_function_table ->
                    responseText.value = model.toString()
                }, failure = {_, m ->
                    responseText.value = m
                })


            }
            "requst contacts permission" -> {


//              if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)  {
//                  Log.w("11111","true")
//              }else{
//                  Log.w("22222","false")
//              }

                CreekManager.sInstance.checkPhoneBookPermissions { it ->
                    Log.w("333333",it.toString())
                    Log.w("1","hahh")
//                    if(!it){
//                        ActivityCompat.requestPermissions(
//                            context as Activity, arrayOf(
//                                "android.permission.READ_CONTACTS",
//                            ), 1
//                        )
//                    }
                }

            }
            "call remind" -> {
                var  operate =  Call.protocol_call_remind()
                operate.contactName =  ByteString.copyFrom("wsl".toByteArray())
                operate.phoneNumber =  ByteString.copyFrom("19200710249".toByteArray())
                CreekManager.sInstance.setCallReminder(model = operate, success = {
                    responseText.value = "success"
                }, failure = {_, m ->
                    responseText.value = m
                })
            }
            "requestNotification" -> {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                startActivity(context,intent,null)
            }

            "requestSMSCallPhone" -> {
                val permissions = arrayOf(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG
                )

                val permissionsToRequest = permissions.filter {
                    context?.let { ctx -> ContextCompat.checkSelfPermission(ctx, it) } != PackageManager.PERMISSION_GRANTED
                }
                if (permissionsToRequest.isNotEmpty()) {
                    ActivityCompat.requestPermissions(context as Activity,
                        permissionsToRequest.toTypedArray(), 123)
                }
            }

            "setSmsToWatchState" -> {
                CreekManager.sInstance.setSmsToWatchState(state = true)

            }
            "Get Watch Sensor" -> {
                CreekManager.sInstance.getWatchSensor({ model ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })
            }
            "Set Watch Sensor" -> {
                val  operate =  WatchSensor.protocol_watch_sensors_operate()
                operate.heartRateAllSwitch = Enums.switch_type.SWITCH_OFF
                operate.bloodOxygenAllSwitch = Enums.switch_type.SWITCH_ON
                CreekManager.sInstance.setWatchSensor(model = operate, success = {
                    responseText.value = "success"
                }, failure = {_, m ->
                    responseText.value = m
                })
            }
            "Get Water Assistant" -> {
                CreekManager.sInstance.getWatchSensor({ model ->
                    responseText.value = model.toString()
                }, failure = { _, m ->
                    responseText.value = m
                })
            }


        }
    }
}