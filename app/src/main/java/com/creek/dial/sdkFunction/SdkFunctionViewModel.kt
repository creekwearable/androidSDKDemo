package com.creek.dial.sdkFunction

import android.util.Log
import androidx.lifecycle.ViewModel

class SdkFunctionViewModel : ViewModel() {

    val functionList = arrayListOf(
        "getLogPath","Binding", "Get Device Information", "Sync", "Upload", "Get Device Bluetooth Status",
        "Get Language", "Set Language", "Sync Time", "Get Time", "Get User Information",
        "Set User Information", "Get Alarm Clock", "Set Alarm Clock", "Get Do Not Disturb",
        "Set Do Not Disturb", "Get Screen Brightness", "Set Screen Brightness",
        "Get Health Monitoring", "Health monitoring setting", "Sleep monitoring acquisition",
        "Sleep monitoring setting", "World clock acquisition", "World clock setting",
        "Message switch query", "Message switch setting", "Message content setting", "Set weather",
        "Incoming call configuration query", "Incoming call configuration settings",
        "Contacts query", "Contacts settings", "Exercise self-identification query",
        "Exercise self-identification settings", "Exercise sub-item data query",
        "Exercise sub-item data setting", "Inquiry about the arrangement order of device exercise",
        "Setting the arrangement order of device exercise",
        "Get the type of exercise supported by the device", "Setting the heart rate interval",
        "Delete the dial", "Query the dial", "Set the dial", "System operation",
        "Query activity data", "Query sleep data", "Query heart rate data", "Query pressure data",
        "Query noise data", "Query blood oxygen data", "Exercise record list",
        "Query exercise details", "Range query exercise record", "Delete exercise record",
        "Off-line ephemeris", "phone book", "setDBUser", "pair"
    )

    fun chooseFunction(index: Int) {
        Log.d("chooseFunction", "chooseFunction: index = $index")
    }

}