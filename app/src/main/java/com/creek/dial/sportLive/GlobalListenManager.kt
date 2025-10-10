package com.creek.dial.sportLive

import com.example.mylibrary.CreekManager
import com.example.proto.Sport


object GlobalListenManager {

    var liveSportDataListenCallback: ((Sport.protocol_exercise_sync_realtime_info) -> Unit)? = null
    var liveSportControlListenCallback: ((Sport.protocol_exercise_control_operate) -> Unit)? = null
    var sportGpsListenCallback: ((Sport.protocol_exercise_gps_info) -> Unit)? = null

    init {
        CreekManager.sInstance.liveSportDataListen { model ->
            liveSportDataListenCallback?.invoke(model)
        }

        CreekManager.sInstance.liveSportControlListen { model ->
            liveSportControlListenCallback?.invoke(model)
        }

        CreekManager.sInstance.sportGpsListen { model ->
            sportGpsListenCallback?.invoke(model)
        }
    }
}
