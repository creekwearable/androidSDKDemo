## andriod Bluetooth Management SDK

### 一、development guide
  
 language support：Java kotlin
  
 minSdk：26
  
 compileSdk：32
    

#### Step 1: Add dependencies

##### build.gradle
    
    dependencies {
    implementation fileTree(dir: 'libs', include: ['*.aar'])
    debugImplementation 'com.example.creek_sdk:flutter_debug:1.0'
    releaseImplementation 'com.example.creek_sdk:flutter_release:1.0'
    implementation 'com.google.code.gson:gson:2.8.8'
    implementation 'com.google.protobuf:protobuf-javalite:4.0.0-rc-2'
    }
##### settings.gradle

       String storageUrl = System.env.FLUTTER_STORAGE_BASE_URL ?: "https://
       storage.googleapis.com"
      repositories {
        maven {
            url "$storageUrl/download.flutter.io"
        }
        maven {
            url '/Users/bean/Documents/项目/creek_sdk_flutter/build/host/outputs/repo'
        }
      }

● Add resource pack CreekSDK.aar

   [CreekSDK.aar](https://creekwearable.github.io/static/andriodSDKVersion/1.0.0/CreekSDK.aar)


#### Step 2：Rights Profile

    <uses-permission
        android:name="android.permission.BLUETOOTH"
        android:maxSdkVersion="30" />
    <uses-permission
        android:name="android.permission.BLUETOOTH_ADMIN"
        android:maxSdkVersion="30" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN"
        android:usesPermissionFlags="neverForLocation"
        tools:targetApi="s" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />


### 二、SDK usage

#### initialization
##### CreekManager.sInstance.initSDK()

#### Whether to support Zilian authorization code verification
##### type 1 verify 0 do not verify
    CreekManager.sInstance.authorization(type = 1)

#### Automatic connection configuration
##### type 1 supports 0 to cancel self-connection
    CreekManager.sInstance.autoConnect(type = 1)
    
#### Monitor Bluetooth device status
        CreekManager.sInstance.listenDeviceState { status, deviceName -> 
            Log.w("listenDeviceState","$status $deviceName") }

#### scanning 
##### ●timeOut  Execution time length to stop scanning optional default 15s

        CreekManager.sInstance.scan(timeOut = 15, devices = { model: Array<ScanDeviceModel> ->
            // Process device list
        } ,endScan = {
           // end of scan
        })
        
#### stop scanning
##### CreekManager.sInstance.stopScan()

#### connect
##### ● id Bluetooth device identification
             CreekManager.sInstance.connect(item.device?.id ?: "", connect = {
                            connectState: Boolean ->
                        if (connectState){
                            
                        }
                    }) 
    
#### Disconnect
                    CreekManager.sInstance.disconnect(success = {
                        viewHolder.connectDeviceView.text = "connect"
                    }, failure = { _, _ ->})
                
#### bind device  
##### ● direct binding
     
      CreekManager.sInstance.bindingDevice(bindType = Enums.bind_method.BIND_NORMAL,
                    id = null, code = null, success = {

                    }, failure = {})
           
           
            
##### ● Pair binding
           ///The first step is to send commands to the firmware, and the firmware displays
            the pairing code 1234
          CreekManager.sInstance.bindingDevice(bindType: Enums.bind_method.BIND_PAIRING_CODE,    
              id: nil, code: nil) {
                ///here is not returned
              } failure: {
                
              }
            
            ///Step 2 Enter the firmware pairing code code: 1234
         CreekManager.sInstance.bindingDevice(bindType: Enums.bind_method.BIND_PAIRING_CODE,
            id: nil, code:"1234") {
                ///Success
            } failure: {
                ///Failure
            }
            
            
### 三、basic instruction  

- []() 
     
  - Get Device Information
     - [getFirmware](#getFirmware)
  - Get Device Bluetooth Status
     - [bluetoothStatus](#bluetoothStatus)
  - Get Language
     - [getLanguage](#getLanguage)
  - Set Language
     - [setLanguage](#setLanguage)
  - Sync Time
     - [syncTime](#syncTime)
  - Get Time
     - [getTime](#getTime)
  - Get User Information
     - [getUserInfo](#getUserInfo)
  - Set User Information
     - [setUserInfo](#setUserInfo)
 - Get Alarm Clock
     - [getAlarm](#getAlarm)
  - Set Alarm Clock
     - [setAlarm](#setAlarm)
  - Get Do Not Disturb
     - [getDisturb](#getDisturb)
  - Set Do Not Disturb
     - [setDisturb](#setDisturb)
  - Get Screen Brightness
     - [getScreen](#getScreen)
  - Set Screen Brightness
     - [setScreen](#setScreen)
  - Get Health Monitoring
     - [getMonitor](#getMonitor)
  - Health monitoring setting
     - [setMonitor](#setMonitor)
  - Sleep monitoring acquisition
     - [getSleepMonitor](#getSleepMonitor)
  - Sleep monitoring setting
     - [setSleepMonitor](#setSleepMonitor)
  - World clock acquisition
     - [getWorldTime](#getWorldTime)
  - World clock setting
     - [setWorldTime](#setWorldTime)
  - Message switch query
     - [getMessageOnOff](#getMessageOnOff)
  - Message switch setting
     - [setMessageOnOff](#setMessageOnOff)
  - Set weather
     - [setWeather](#setWeather)
  - Incoming call configuration query
     - [getCall](#getCall)
  - Incoming call configuration settings
     - [setCall](#setCall)
  - Contacts query
     - [getContacts](#getContacts)
  - Contacts settings
     - [setContacts](#setContacts)
  - Exercise self-identification query
     - [getSportIdentification](#getSportIdentification)
  - Exercise self-identification settings
     - [setSportIdentification](#setSportIdentification)
  - Exercise sub-item data query
     - [getSportSub](#getSportSub)
  - Exercise sub-item data setting
     - [setSportSub](#setSportSub)
  - Inquiry about the arrangement order of device exercise
     - [getSportSort](#getSportSort)     
  - Setting the arrangement order of device exercise
     - [setSportSort](#setSportSort)
  - Get the type of exercise supported by the device
     - [getSportType](#getSportType)
  - Setting the heart rate interval
     - [setSportHeartRate](#setSportHeartRate)
  - Delete the dial
     - [delWatchDial](#delWatchDial)     
  - Query the dial
     - [getWatchDial](#getWatchDial)    
  - Set the dial
     - [setWatchDial](#setWatchDial)     
  - System operation
     - [setSystem](#setSystem)       
     
     
     
     
    
####  <a name="getFirmware"></a> Get Device Information
   
                   CreekManager.sInstance.getFirmware({
                       model: Deviceinfo.protocol_device_info ->
                       textView.text = model.toString()

                   }, failure = {
                       c,m ->
                   })
            
            
    
#### <a name="bluetoothStatus"></a> Get Device Bluetooth Status
            
                    CreekManager.sInstance.bluetoothStatus({
                            model: Mtu.protocol_connect_status_inquire_reply ->
                        textView.text = model.toString()

                    }, failure = {
                            _,m ->
                        textView.text = m
                    })
            
#### <a name="getLanguage"></a> Get Language
            
                    CreekManager.sInstance.getLanguage({
                            model: Language.protocol_language_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
            
#### <a name="setLanguage"></a> Set Language
            
                    CreekManager.sInstance.setLanguage(type = Enums.language.CHINESE, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
#### <a name="syncTime"></a> Sync Time
            
                    CreekManager.sInstance.syncTime(success = {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
            
#### <a name="getTime"></a> Get Time
            
                    CreekManager.sInstance.getTime({
                            model: Firmwaretime.protocol_device_time_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
            
#### <a name="getUserInfo"></a> Get User Information
           
                    CreekManager.sInstance.getUserInfo({
                        model: Userinfo.protocol_user_info_operate ->
                         textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )       
            
#### <a name="setUserInfo"></a> Set User Information
           
                    CreekManager.sInstance.getUserInfo({
                            model: Userinfo.protocol_user_info_operate ->
                        model.personalInfo.year = 2024
                        model.personalInfo.month = 11
                        model.goalSetting.workoutDay = 7
                        model.goalSetting.steps = 100
                        model.goalSetting.notifyFlag = Enums.notify_type.CLOSE
                        CreekManager.sInstance.setUserInfo(model = model,{
                            textView.text = "success"
                        }, failure = {
                            c,m ->
                            textView.text = m

                        })

                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )       
   
#### <a name="getAlarm"></a> Get Alarm Clock
           
                    CreekManager.sInstance.getAlarm({
                            model: Alarm.protocol_alarm_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
               
  
#### <a name="setAlarm"></a> Set Alarm Clock
           
                       CreekManager.sInstance.getAlarm({
                            model: Alarm.protocol_alarm_inquire_reply ->
                        var data = Alarm.protocol_alarm_operate()
                        data.addAllAlarmItem(model.alarmItemList)
                        var item =  Alarm.protocol_set_alarm_item()
                        item.alarmId = 1;
                        item.dispStatus = Enums.disp_status.DISP_ON
                        item.type = Enums.alarm_type.GET_UP
                        item.hour = 22
                        item.minute = 30
                        item.addAllRepeat(listOf(true,true,true,true,true,false,false))
                        item.switchFlag = false
                        item.laterRemindRepeatTimes = 1
                        item.vibrateOnOff = true
                        item.name = ByteString.copyFrom("abc".toByteArray())
                        data.addAlarmItem(item)
                        CreekManager.sInstance.setAlarm(model = data,{
                            textView.text = "success"
                        }, failure = {
                                c,m ->
                            textView.text = m

                        })

                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
                
#### <a name="getDisturb"></a> Get Do Not Disturb
           
                    CreekManager.sInstance.getDisturb({
                            model: Disturb.protocol_disturb_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
              
#### <a name="setDisturb"></a> Set Do Not Disturb
           
                     var model = Disturb.protocol_disturb_operate()
                    model.disturbOnOff = true
                    CreekManager.sInstance.setDisturb(model =model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
            
            
#### <a name="getScreen"></a> Get Screen Brightness
           
                    CreekManager.sInstance.getScreen({
                            model: Screen.protocol_screen_brightness_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
#### <a name="setScreen"></a> Set Screen Brightness
           
                    var model = Screen.protocol_screen_brightness_operate()
                    model.nightAutoAdjust.startHour = 20
                    CreekManager.sInstance.setScreen(model =model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
           
           
#### <a name="getScreen"></a> Get Health Monitoring
           
                    var model = Monitor.protocol_health_monitor_operate()
                    model.healthType = Enums.health_type.HEART_RATE

                    CreekManager.sInstance.getMonitor(operate = model,{
                            model: Monitor.protocol_health_monitor_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
#### <a name="setScreen"></a> Health monitoring setting
           
                    var model = Monitor.protocol_health_monitor_operate()
                    model.healthType = Enums.health_type.HEART_RATE
                    CreekManager.sInstance.setMonitor(model =model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } ) 
            
#### <a name="getSleepMonitor"></a> Sleep monitoring acquisition
           
                    CreekManager.sInstance.getSleepMonitor({
                            model: SleepMonitor.protocol_sleep_monitor_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
#### <a name="setSleepMonitor"></a> Sleep monitoring setting
           
                    var model = SleepMonitor.protocol_sleep_monitor_operate()
                    model.switchFlag = true
                    CreekManager.sInstance.setSleepMonitor(model =model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
            
#### <a name="getWorldTime"></a> World clock acquisition
           
                    CreekManager.sInstance.getWorldTime({
                            model: Wordtime.protocol_world_time_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
#### <a name="setWorldTime"></a> World clock setting
           
                    var model = Wordtime.protocol_world_time_operate()
                    var item = Wordtime.protocol_world_time_item()
                    item.cityName = ByteString.copyFrom("shenzheng".toByteArray())
                    item.offestMin = 120
                    model.addWorldTimeItem(item)
                    CreekManager.sInstance.setWorldTime(model =model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
            
#### <a name="getMessageOnOff"></a> Message switch query
           
                    CreekManager.sInstance.getMessageOnOff({
                            model: Message.protocol_message_notify_switch_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
#### <a name="setMessageOnOff"></a> Message switch setting
           
                    var model = Message.protocol_message_notify_switch()
                     model.notifySwitch = true
                    CreekManager.sInstance.setMessageOnOff(model =model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )        
#### <a name="setWeather"></a> Set weather
           
                    var model = Weather.protocol_weather_operate()
                    model.switchFlag = true
                    var item = Weather.protocol_weather_detail_data_item()
                    item.hour = 14
                    item.curTemp = 30
                    item.curMaxTemp = 33
                    item.curMinTemp = 26
                    model.addDetailDataItem(item)
                    CreekManager.sInstance.setWeather(model =model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )                              
                            
#### <a name="getCall"></a> Incoming call configuration query
           
                    CreekManager.sInstance.getCall({
                            model: Call.protocol_call_switch_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
#### <a name="setCall"></a> Incoming call configuration settings
           
                    var model = Call.protocol_call_switch()
                    model.callSwitch = true
                    model.callDelay = 5
                    CreekManager.sInstance.setCall(model =model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )                           
                                                 
                            
#### <a name="getContacts"></a> Contacts query
           
             CreekInterFace.instance.getContacts { model in
                            } failure: { code, message in
               
            }
              
#### <a name="setContacts"></a> Contacts settings
           
            var data =  protocol_frequent_contacts_operate()
            var item =  protocol_frequent_contacts_item()
            item.phoneNumber = "12345678912".data(using: .utf8)!
            item.contactName = "bean".data(using: .utf8)!
            data.contactsItem.append(item)
            CreekInterFace.instance.setContacts(model: data) {
               
            } failure: { code, message in
                
            }                             
                            
#### <a name="getSportIdentification"></a> Exercise self-identification query
           
                    CreekManager.sInstance.getSportIdentification({
                            model:  Sport.protocol_exercise_intelligent_recognition_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
#### <a name="setSportIdentification"></a> Exercise self-identification settings
           
                    var model = Sport.protocol_exercise_intelligent_recognition()
                    model.walkTypeSwitch = true
                    CreekManager.sInstance.setSportIdentification(model = model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )                
                            
#### <a name="getSportSub"></a> Exercise sub-item data query
           
                    CreekManager.sInstance.getSportSub({
                            model: Sport.protocol_exercise_sporting_param_sort_inquire_reply 
                             ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )
              
#### <a name="setSportSub"></a> Exercise sub-item data setting
           
                    var model = Sport.protocol_exercise_sporting_param_sort()
                   model.sportType = Enums.sport_type.BARBELL.value
                    CreekManager.sInstance.setSportSub(model = model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )               
                            
#### <a name="getSportSort"></a> Inquiry about the arrangement order of device exercise
           
                    CreekManager.sInstance.getSportSort({
                            model: Sport.protocol_exercise_sport_mode_sort_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )

              
#### <a name="setSportSort"></a> Setting the arrangement order of device exercise
           
                    var model = Sport.protocol_exercise_sport_mode_sort()
                   model.addSportItems(Enums.sport_type.BADMINTON)
                    CreekManager.sInstance.setSportSort(model = model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )               
                            
#### <a name="getSportType"></a> Get the type of exercise supported by the device
           
                    CreekManager.sInstance.getSportType({
                            model: Sport.protocol_exercise_sporting_param_sort_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )

              
#### <a name="setSportHeartRate"></a> Setting the heart rate interval
           
                    var model = Sport.protocol_exercise_heart_rate_zone()
                    model.zone1 = 100
                    model.zone2 = 100
                    model.zone3 = 100
                    model.zone4 = 100
                    model.zone5 = 100

                    CreekManager.sInstance.setSportHeartRate(model = model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )                 

              
#### <a name="delWatchDial"></a> Delete the dial
           
                    var model = Watchdial.protocol_watch_dial_plate_operate()
                    model.addDialName(ByteString.copyFrom("1".toByteArray()))
                    CreekManager.sInstance.delWatchDial(model = model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )    
                            
#### <a name="getWatchDial"></a> Query the dial
           
                    CreekManager.sInstance.getWatchDial({
                            model: Watchdial.protocol_watch_dial_plate_inquire_reply ->
                        textView.text = model.toString()
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )

              
#### <a name="setWatchDial"></a> Set the dial
           
                    var model = Watchdial.protocol_watch_dial_plate_operate()
                    model.addDialName(ByteString.copyFrom("1".toByteArray()))
                    CreekManager.sInstance.setWatchDial(model = model, {
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )              
                            
#### <a name="setSystem"></a> System operation
           
                    CreekManager.sInstance.setSystem(type = 1,{
                        textView.text = "success"
                    }, failure = {
                            _,m ->
                        textView.text = m
                    } )                 
                            
### 三、Query local data                           
                            
   - Query activity data
     - [getActivityNewTimeData](#getActivityNewTimeData)     
  - Query sleep data
     - [getSleepNewTimeData](#getSleepNewTimeData)    
  - Query heart rate data
     - [getHeartRateNewTimeData](#getHeartRateNewTimeData)     
  - Query pressure data
     - [getStressNewTimeData](#getStressNewTimeData)    
  - Query noise data
     - [getNoiseNewTimeData](#getNoiseNewTimeData)     
  - Query blood oxygen data
     - [getSpoNewTimeData](#getSpoNewTimeData)    
  - Exercise record list
     - [getSportRecord](#getSportRecord)     
  - Query exercise details
     - [getSportDetails](#getSportDetails)    
  - Range query exercise record
     - [getSportTimeData](#getSportTimeData)     
  - Delete exercise record
     - [delSportRecord](#delSportRecord)    
                          
                            
#### <a name="getActivityNewTimeData"></a> Query activity data
           
                    CreekManager.sInstance.getActivityNewTimeData(startTime = "2023-08-01", 
                    endTime = "2023-09-01") {
                            model: BaseModel<Array<ActivityModel>> ->

                        textView.text = model.data?.toList().toString()

                    }
              
#### <a name="getSleepNewTimeData"></a> Query sleep data
           
                    CreekManager.sInstance.getSleepNewTimeData(startTime = "2023-08-01", 
                    endTime = "2023-09-01") {
                            model: BaseModel<Array<SleepModel>> ->
                        textView.text = model.data?.toList().toString()

                    }        
                            
#### <a name="getHeartRateNewTimeData"></a> Query heart rate data
           
                    CreekManager.sInstance.getHeartRateNewTimeData(startTime = "2023-08-01", 
                    endTime = "2023-09-01") {
                            model: BaseModel<Array<HeartRateModel>> ->
                        textView.text = model.data?.toList().toString()

                    }

              
#### <a name="getStressNewTimeData"></a> Query pressure data
           
                    CreekManager.sInstance.getStressNewTimeData(startTime = "2023-08-01", 
                    endTime = "2023-09-01") {
                            model: BaseModel<Array<StressModel>> ->
                        textView.text = model.data?.toList().toString()

                    }               
                            
#### <a name="getNoiseNewTimeData"></a> Query noise data
           
                    CreekManager.sInstance.getNoiseNewTimeData(startTime = "2023-08-01", endTime = "2023-09-01") {
                            model: BaseModel<Array<NoiseModel>> ->
                        textView.text = model.data?.toList().toString()

                    }

              
#### <a name="getSpoNewTimeData"></a> Query blood oxygen data
           
                    CreekManager.sInstance.getSpoNewTimeData(startTime = "2023-08-01", endTime = "2023-09-01") {
                            model: BaseModel<Array<OxygenModel>> ->
                        textView.text = model.data?.toList().toString()

                    }                 

              
#### <a name="getSportRecord"></a> Exercise record list
           
                    CreekManager.sInstance.getSportRecord(type = null) {
                            model: BaseModel<Array<SportModel>> ->
                        textView.text = model.data?.toList().toString()

                    }      
                            
#### <a name="getSportDetails"></a> Query exercise details
           
                    CreekManager.sInstance.getSportDetails(id = 1) {
                            model: BaseModel<SportModel> ->
                        textView.text = model.data?.toString()

                    }

              
#### <a name="getSportTimeData"></a> Range query exercise record
           
                    CreekManager.sInstance.getSportTimeData(startTime = "2023-08-01", endTime
                     = "2023-09-01", type = null) {
                            model: BaseModel<Array<SportModel>> ->
                        textView.text = model.data?.toList().toString()

                    }               
                            
#### <a name="delSportRecord"></a> Delete exercise record
           
                    CreekManager.sInstance.delSportRecord(id = 1) {
                            model: BaseModel<BaseDataModel> ->
                        textView.text = model.toString()

                    }                        
                            
                            
                            
                            
                            
                            
                            
                            