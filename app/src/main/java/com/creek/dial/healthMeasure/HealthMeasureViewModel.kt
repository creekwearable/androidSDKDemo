package com.creek.dial.healthMeasure

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylibrary.CreekManager
import com.example.proto.CommonErrorOuterClass
import com.example.proto.Enums
import com.example.proto.Ring
import kotlinx.coroutines.launch



class HealthMeasureViewModel : ViewModel() {
    var selectedType by mutableStateOf(Enums.ring_health_type.RING_HEART_RATE)
    var statusText by mutableStateOf("状态：未开始")
    var resultText by mutableStateOf("--")

    fun startMeasure() {
        statusText = "状态：测量中..."
        resultText = "--"
        viewModelScope.launch {

            CreekManager.sInstance.startMeasure(
                type = selectedType,
                model =  { model: Ring.protocol_ring_click_measure_operate ->
                    statusText = "状态：测量完成 ✅"
                    resultText = "结果：${model.value}"
                },
                failure =  { error: CommonErrorOuterClass.CommonError ->
                    statusText = "错误：${error.message}"
                }
            )

        }
    }
    fun stopMeasure() {
        statusText = "状态：已停止测量 ⛔️"
        CreekManager.sInstance.stopMeasure(selectedType)
    }
}
