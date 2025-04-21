package com.creek.dial.scanDevice

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ScanDeviceModel
import com.example.mylibrary.CreekManager
import com.example.proto.Enums
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn



class ScanDeviceViewModel : ViewModel() {

    var deviceList = mutableStateListOf<ScanDeviceModel>()
    var connectDeviceId: MutableState<String> = mutableStateOf("")

    init {
        scanDevice()
    }

    fun scanDevice() {
        CreekManager.sInstance.scan(timeOut = 15, devices = { model: List<ScanDeviceModel> ->
            deviceList.clear()
            deviceList.addAll(model)
        } ,endScan = {
            Log.w("111","jjjj")
        })
    }

    fun connect(deviceId:String) {
      CreekManager.sInstance.connect(id = deviceId, connect = {
          if (it){
              connectDeviceId.value =  deviceId
              CreekManager.sInstance.bindingDevice(
                  bindType = Enums.bind_method.BIND_NORMAL,
                  id = null,
                  code = null,
                  success = {
                      connectDeviceId.value =  deviceId
                  },
                  failure = {

                  })

          }else{
              connectDeviceId.value = ""
          }
      })
    }

}


