package com.creek.dial.sportLive


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylibrary.CreekManager
import com.example.proto.Enums
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SportsLiveViewModel : ViewModel() {

    private val _state = MutableStateFlow("state：")
    val state = _state.asStateFlow()

    private val _jsonText = MutableStateFlow("")
    val jsonText = _jsonText.asStateFlow()

    private val _sportTypes = MutableStateFlow<List<Enums.sport_type>>(emptyList())
    val sportTypes = _sportTypes.asStateFlow()

    private val _currentType = MutableStateFlow(Enums.sport_type.ORUN)
    val currentType = _currentType.asStateFlow()

    init {

        GlobalListenManager.liveSportDataListenCallback = { model ->
            val json = model.toString()
            _jsonText.value = json
        }

        GlobalListenManager.liveSportControlListenCallback = { model ->
            _state.value = "state：${model.controlType.name.uppercase()}"
        }

        GlobalListenManager.sportGpsListenCallback = { /* handled globally */ }

        loadSportTypes()
    }

    private fun loadSportTypes() {
        CreekManager.sInstance.getSportType({ model ->
            viewModelScope.launch {
                _sportTypes.value = model.supportTypeList
                if (model.supportTypeList.isNotEmpty()) {
                    _currentType.value = model.supportTypeList.first()
                }
            }
        }, { _, message -> println("getSportType failure: $message") })
    }

    fun startSport() = sendControl(Enums.exercise_control_type.CONTROL_START)
    fun pauseSport() = sendControl(Enums.exercise_control_type.CONTROL_PAUSE)
    fun resumeSport() = sendControl(Enums.exercise_control_type.CONTROL_RESUME)
    fun endSport() = sendControl(Enums.exercise_control_type.CONTROL_END)

    private fun sendControl(type: Enums.exercise_control_type) {
        _state.value = "loading..."
        CreekManager.sInstance.setSportControl(type, _currentType.value, {
            _state.value = "state：${type.name.uppercase()}"
        }, { _, message ->
            _state.value = "error：$message"
        })
    }

    fun selectSportType(type: Enums.sport_type) {
        _currentType.value = type
    }
}
