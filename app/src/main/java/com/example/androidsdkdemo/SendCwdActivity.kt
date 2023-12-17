package com.example.androidsdkdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.model.*
import com.example.mylibrary.CreekManager
import com.example.mylibrary.SportTypeDeserializer
import com.example.mylibrary.SportTypeSerializer
import com.example.mylibrary.sportType
import com.example.proto.*
import com.google.gson.GsonBuilder
import com.google.protobuf.ByteString


class SendCwdActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "操作平台"
        setContentView(R.layout.activity_cwd)
        var iconBack = findViewById<ImageView>(R.id.icon_back)
        iconBack.setOnClickListener {
            finish()
        }
        var cmd = findViewById<Button>(R.id.sendCWD)
        var textView = findViewById<TextView>(R.id.content)


        cmd.setOnClickListener {
            val value = intent.getStringExtra("name")
            when (value) {
                "Get Device Information" -> {
                    CreekManager.sInstance.getFirmware({ model: Deviceinfo.protocol_device_info ->
                        textView.text = model.toString()
                        CreekManager.sInstance.getSNFirmware(model,{
                            Log.w("sn", "sn码++++$it")
                        })

                    }, failure = { c, m ->
                    })

                }
                "Get Device Bluetooth Status" -> {

                    CreekManager.sInstance.bluetoothStatus({ model: Mtu.protocol_connect_status_inquire_reply ->

                        textView.text = model.toString()

                    }, failure = { _, m ->

                        textView.text = m
                    })

                }
                "Sync Time" -> {
                    CreekManager.sInstance.syncTime(success = {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Get Time" -> {
                    CreekManager.sInstance.getTime({ model: Time.protocol_device_time_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Get User Information" -> {
                    CreekManager.sInstance.getUserInfo({ model: Userinfo.protocol_user_info_operate ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
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
                            textView.text = "success"
                        }, failure = { c, m ->
                            textView.text = m

                        })

                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Get Alarm Clock" -> {
                    CreekManager.sInstance.getAlarm({ model: Alarm.protocol_alarm_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
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
                        data.addAlarmItem(item)
                        CreekManager.sInstance.setAlarm(model = data, {
                            textView.text = "success"
                        }, failure = { c, m ->
                            textView.text = m

                        })

                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Get Do Not Disturb" -> {
                    CreekManager.sInstance.getDisturb({ model: Disturb.protocol_disturb_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Set Do Not Disturb" -> {
                    var model = Disturb.protocol_disturb_operate()
                    model.disturbOnOff = true
                    CreekManager.sInstance.setDisturb(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Get Screen Brightness" -> {
                    CreekManager.sInstance.getScreen({ model: Screen.protocol_screen_brightness_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Set Screen Brightness" -> {
                    var model = Screen.protocol_screen_brightness_operate()
                    model.nightAutoAdjust.startHour = 20
                    CreekManager.sInstance.setScreen(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Get Health Monitoring" -> {
                    var model = Monitor.protocol_health_monitor_operate()
                    model.healthType = Enums.health_type.HEART_RATE

                    CreekManager.sInstance.getMonitor(
                        operate = model,
                        { model: Monitor.protocol_health_monitor_inquire_reply ->
                            textView.text = model.toString()
                        },
                        failure = { _, m ->
                            textView.text = m
                        })

                }
                "Health monitoring setting" -> {
                    var model = Monitor.protocol_health_monitor_operate()
                    model.healthType = Enums.health_type.HEART_RATE
                    CreekManager.sInstance.setMonitor(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Sleep monitoring acquisition" -> {
                    CreekManager.sInstance.getSleepMonitor({ model: SleepMonitor.protocol_sleep_monitor_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Sleep monitoring setting" -> {
                    var model = SleepMonitor.protocol_sleep_monitor_operate()
                    model.switchFlag = true
                    CreekManager.sInstance.setSleepMonitor(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "World clock setting" -> {
                    var model = Wordtime.protocol_world_time_operate()
                    var item = Wordtime.protocol_world_time_item()
                    item.cityName = ByteString.copyFrom("shenzheng".toByteArray())
                    item.offestMin = 120
                    model.addWorldTimeItem(item)
                    CreekManager.sInstance.setWorldTime(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "World clock acquisition" -> {
                    CreekManager.sInstance.getWorldTime({ model: Wordtime.protocol_world_time_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Message switch query" -> {
                    CreekManager.sInstance.getMessageOnOff({ model: Message.protocol_message_notify_switch_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Message switch setting" -> {
                    var model = Message.protocol_message_notify_switch()
                    model.notifySwitch = true
                    CreekManager.sInstance.setMessageOnOff(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
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
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
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
                    model.addDetailDataItem(item)
                    CreekManager.sInstance.setWeather(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Incoming call configuration query" -> {
                    CreekManager.sInstance.getCall({ model: Call.protocol_call_switch_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })


                }
                "Incoming call configuration settings" -> {
                    var model = Call.protocol_call_switch()
                    model.callSwitch = true
                    model.callDelay = 5
                    CreekManager.sInstance.setCall(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Contacts query" -> {
                    CreekManager.sInstance.getContacts({ model: Contacts.protocol_frequent_contacts_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Contacts settings" -> {
                    var model = Contacts.protocol_frequent_contacts_operate()
                    var item = Contacts.protocol_frequent_contacts_item()
                    item.phoneNumber = ByteString.copyFrom("12345678912".toByteArray())
                    item.contactName = ByteString.copyFrom("bean".toByteArray())
                    model.addContactsItem(item)
                    CreekManager.sInstance.setContacts(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Exercise self-identification query" -> {
                    CreekManager.sInstance.getSportIdentification({ model: Sport.protocol_exercise_intelligent_recognition_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Exercise self-identification settings" -> {
                    var model = Sport.protocol_exercise_intelligent_recognition()
                    model.walkTypeSwitch = true
                    CreekManager.sInstance.setSportIdentification(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Exercise sub-item data query" -> {
                    CreekManager.sInstance.getSportSub({ model: Sport.protocol_exercise_sporting_param_sort_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Exercise sub-item data setting" -> {
                    var model = Sport.protocol_exercise_sporting_param_sort()
                    model.sportType = Enums.sport_type.BARBELL.value
                    CreekManager.sInstance.setSportSub(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Inquiry about the arrangement order of device exercise" -> {
                    CreekManager.sInstance.getSportSort({ model: Sport.protocol_exercise_sport_mode_sort_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Setting the arrangement order of device exercise" -> {
                    var model = Sport.protocol_exercise_sport_mode_sort()
                    model.addSportItems(Enums.sport_type.BADMINTON)
                    CreekManager.sInstance.setSportSort(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Get the type of exercise supported by the device" -> {
                    CreekManager.sInstance.getSportType({ model: Sport.protocol_exercise_func_support_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
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
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Delete the dial" -> {
                    var model = Watchdial.protocol_watch_dial_plate_operate()
                    model.addDialName(ByteString.copyFrom("1".toByteArray()))
                    CreekManager.sInstance.delWatchDial(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Set the dial" -> {
                    var model = Watchdial.protocol_watch_dial_plate_operate()
                    model.addDialName(ByteString.copyFrom("1".toByteArray()))
                    CreekManager.sInstance.setWatchDial(model = model, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Query the dial" -> {
                    CreekManager.sInstance.getWatchDial({ model: Watchdial.protocol_watch_dial_plate_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Set Language" -> {
                    CreekManager.sInstance.setLanguage(type = Enums.language.CHINESE, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })
                }
                "Get Language" -> {
                    CreekManager.sInstance.getLanguage({ model: Language.protocol_language_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "System operation" -> {
                    CreekManager.sInstance.setSystem(type = 1, {
                        textView.text = "success"
                    }, failure = { _, m ->
                        textView.text = m
                    })

                }
                "Query activity data" -> {
                    CreekManager.sInstance.getActivityNewTimeData(
                        startTime = "2023-10-01",
                        endTime = "2023-11-23"
                    ) { model: BaseModel<List<ActivityModel>> ->

                        textView.text = model.data?.toList().toString()

                    }

//

                }
                "Query sleep data" -> {
                    CreekManager.sInstance.getSleepNewTimeData(
                        startTime = "2023-08-01",
                        endTime = "2023-09-01"
                    ) { model: BaseModel<List<SleepModel>> ->
                        textView.text = model.data?.toList().toString()

                    }

                }
                "Query heart rate data" -> {
                    CreekManager.sInstance.getHeartRateNewTimeData(
                        startTime = "2023-08-01",
                        endTime = "2023-09-01"
                    ) { model: BaseModel<List<HeartRateModel>> ->
                        textView.text = model.data?.toList().toString()

                    }

                }
                "Query pressure data" -> {
                    CreekManager.sInstance.getStressNewTimeData(
                        startTime = "2023-08-01",
                        endTime = "2023-09-01"
                    ) { model: BaseModel<List<StressModel>> ->
                        textView.text = model.data?.toList().toString()

                    }

                }
                "Query noise data" -> {
                    CreekManager.sInstance.getNoiseNewTimeData(
                        startTime = "2023-08-01",
                        endTime = "2023-09-01"
                    ) { model: BaseModel<List<NoiseModel>> ->
                        textView.text = model.data?.toList().toString()

                    }
                }

                "Query blood oxygen data" -> {
                    CreekManager.sInstance.getSpoNewTimeData(
                        startTime = "2023-08-01",
                        endTime = "2023-09-01"
                    ) { model: BaseModel<List<OxygenModel>> ->
                        textView.text = model.data?.toList().toString()

                    }

                }
                "Exercise record list" -> {
                    CreekManager.sInstance.getSportRecord(type = null) { model: BaseModel<List<SportModel>> ->
                        textView.text = model.data?.toList().toString()

                    }

                }
                "Query exercise details" -> {
                    CreekManager.sInstance.getSportDetails(id = 1) { model: BaseModel<SportModel> ->
                        textView.text = model.data?.toString()

                    }

                }
                "Range query exercise record" -> {
                    CreekManager.sInstance.getSportTimeData(
                        startTime = "2023-08-01",
                        endTime = "2023-11-20",
                        type = null
                    ) { model: BaseModel<List<SportModel>> ->
//                        textView.text = model.data?.toList().toString()
                        val gson = GsonBuilder()
                            .registerTypeAdapter(sportType::class.java, SportTypeSerializer())
                            .create()
                        textView.text = gson.toJson(model)
//

                    }

                }
                "Delete exercise record" -> {
                    CreekManager.sInstance.delSportRecord(id = 1) { model: BaseModel<BaseDataModel> ->
                        textView.text = model.toString()

                    }

                }

            }
        }

    }


}