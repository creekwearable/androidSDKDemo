package com.notification_listener_util.music

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.util.Log
import io.flutter.BuildConfig
import java.util.HashMap

class SessionCallbacks internal constructor(private var mediaController: MediaController) :
    MediaController.Callback() {

     companion object {
         @JvmStatic
         private val TAG = "lxk-SessionCallbacks"
     }


    //creekMediaControllerUtils
    override fun onMetadataChanged(metadata: MediaMetadata?) {
        super.onMetadataChanged(metadata)
        if (metadata != null) {
            CreekMediaControllerUtils.getInstance().setController(mediaController)
            ///获取音乐信息发送
            CreekMediaControllerUtils.getInstance().getMusicInfoSendToCreekWatch()
        }
    }

    ///播放状态改变
    override fun onPlaybackStateChanged(state: PlaybackState?) {
        super.onPlaybackStateChanged(state)
        if (state != null) {
            var isStopped = false
            val isPlaying = state.state == 3
            val isPaused = state.state == 2
            if (state.state == 1) {
                isStopped = true
            }
            CreekMediaControllerUtils.getInstance().setController(mediaController)
            if (isPlaying || isPaused) {
                CreekMediaControllerUtils.getInstance().getMusicInfoSendToCreekWatch()
            }  else if (isStopped) {
                CreekMediaControllerUtils.getInstance().clearMusicInfoSendToCreekWatch()
            }
            if(BuildConfig.DEBUG){
                Log.e(
                    TAG,
                    "MediaController.Callback onPlaybackStateChanged isPlaying: $isPlaying"
                )
            }

        }
    }

    override fun onSessionDestroyed() {
        super.onSessionDestroyed()

        Log.e(TAG, "MediaController.Callback onSessionDestroyed ")

        mediaController.unregisterCallback(this)

        CreekMediaControllerUtils.getInstance().removeMediaController(mediaController.packageName)

        CreekMediaControllerUtils.getInstance().clearMusicInfoSendToCreekWatch()
    }

    override fun onAudioInfoChanged(info: MediaController.PlaybackInfo?) {
        super.onAudioInfoChanged(info)

        Log.d(TAG, "MediaController.Callback onAudioInfoChanged--${info?.toString()} ")
        if (info != null) {
            val volumeControl: Int = info.volumeControl
            val maxVolume: Int = info.maxVolume
            val currentVolume: Int = info.currentVolume
            val packageName = mediaController.packageName
            Log.i(TAG, "---------------------------------")
            Log.i(TAG, "| maxVolume: $maxVolume")
            Log.i(TAG, "| currentVolume: $currentVolume")
            Log.i(TAG, "| packageName: $packageName")
            Log.i(TAG, "| volumeControl: ${volumeControl.toString()}")
            Log.i(TAG, "---------------------------------")
        }
    }


}