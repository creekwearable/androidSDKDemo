package com.notification_listener_util.music

import android.annotation.SuppressLint
import android.content.*
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService.MEDIA_SESSION_SERVICE
import android.util.Log
import com.example.mylibrary.CreekManager
import com.example.proto.Enums
import com.example.proto.Music
import com.google.protobuf.ByteString
import com.notification_listener_util.data.MusicBean
import kotlin.math.round


class CreekMediaControllerUtils {
    private var laseMusicBean: MusicBean? = null
    var mMediaController: MediaController? = null
    private val linkedHashMaps = LinkedHashMap<String, MediaController.Callback?>()
    private var mAudioManager: AudioManager? = null
    private var mContext: Context? = null
    private var isActivityMediaSessionManager = false
    private var volumeChangeReceiver: VolumeChangeReceiver? = null
    private var lastStatus: Int = -1

    companion object {
        @JvmStatic
        private val TAG = "lxk-CreekMediaControllerUtils"

        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        private var mInstance: CreekMediaControllerUtils? = null

        fun getInstance(): CreekMediaControllerUtils {
            return mInstance ?: synchronized(this) {
                mInstance ?: CreekMediaControllerUtils().also {
                    mInstance = it

                }
            }
        }
    }


    private fun registerVolumeReceiver() {
        try {
            if (volumeChangeReceiver == null) {
                Log.e(TAG, "registerVolumeReceiver-start")
                volumeChangeReceiver = VolumeChangeReceiver()
                val filter = IntentFilter()
                filter.addAction("android.media.VOLUME_CHANGED_ACTION")
                mContext?.registerReceiver(volumeChangeReceiver, filter)
            }
        } catch (e: Exception) {
            Log.e(TAG, "registerVolumeReceiver-error:${e.message}")
        }


    }

    fun unRegisterVolumeReceiver() {
        try {
            if (volumeChangeReceiver != null) {
                Log.e(TAG, "unregisterReceiver-start")
                mContext?.unregisterReceiver(volumeChangeReceiver)
                volumeChangeReceiver = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "ouRegisterVolumeReceiver-error:${e.message}")
        }


    }

    ///清除所有的回调
    fun clearMediaControllerCallback() {
        linkedHashMaps.clear()
    }

    ///音量管理器
    fun setAudioManager(audioManager: AudioManager) {
        this.mAudioManager = audioManager
    }

    fun setContext(context: Context) {

        mContext = context

    }

    fun setController(controller: MediaController?) {

        this.mMediaController = controller

    }

    fun pauseMusic() {
        mMediaController?.transportControls?.pause()

    }

    fun startPlayMusic() {

        mMediaController?.transportControls?.play()

    }

    fun getActivityMediaSessionManagerState(): Boolean {
        return isActivityMediaSessionManager
    }

    fun previousSong() {

        mMediaController?.transportControls?.skipToPrevious()

    }

    fun nextSong() {


        mMediaController?.transportControls?.skipToNext()

    }

    fun setVolume(volume: Double): Boolean {
        if (mMediaController != null) {
            var volumePercentage = 0.0
            volumePercentage = volume
            val maxVolumeTemp = mMediaController!!.playbackInfo?.maxVolume
            val maxVolumeTemp2 = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            Log.w(TAG, "$maxVolumeTemp2--setVolume( volume:Double):$maxVolumeTemp")
            val maxVolume: Int = mMediaController!!.playbackInfo?.maxVolume ?: mAudioManager?.getStreamMaxVolume(
                AudioManager.STREAM_MUSIC
            ) ?: 0
            if (maxVolume == 0) {
                return false
            }
            val volumeTemp = (round(volumePercentage * maxVolume)).toInt()

            mMediaController?.setVolumeTo(volumeTemp, AudioManager.FLAG_SHOW_UI)
            return true
        }
        Log.w("lxk", "setVolume-mMediaController==null")
        return false
    }

    fun lowerVolume() {
        mAudioManager?.adjustStreamVolume(3, -1, AudioManager.FLAG_PLAY_SOUND)
        getMusicInfoSendToCreekWatch()
    }

    fun getVolume(): Int {
        return mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
    }

    fun getMusicInfoSendToCreekWatch() {
        val musicBean: MusicBean? = getMusicInfo()
        if (musicBean != null) {
            sendMusicInfoToWatch(musicBean)
        }
    }

    private fun sendMusicInfoToWatch(musicBean: MusicBean) {
        val currentStatus = musicBean.getPlayState()
        val isResumeFromPause = lastStatus == 2 && currentStatus == 3 // Resume from pause
        // Determine whether the content is consistent (curTime and totalTime are ignored)
        val isSameContent = laseMusicBean?.let {
            it.getMusicName() == musicBean.getMusicName()
                    && it.getSingerName() == musicBean.getSingerName()
                    && it.getPlayState() == musicBean.getPlayState()
                    && it.getCurrentVolume() == musicBean.getCurrentVolume()
        } ?: false

        //If the content is the same and not resumed from pause, no need to send
        if (isSameContent && !isResumeFromPause) {
            return
        }
        // update status
        laseMusicBean = musicBean
        if (currentStatus != null) {
            lastStatus = currentStatus
        }

        val operate = Music.protocol_music_control_operate().apply {
            switchFlag = true
            status = if (musicBean.getPlayState() == 2)
                Enums.music_status.MUSIC_STATUS_PAUSE
            else
                Enums.music_status.MUSIC_STATUS_PLAY

            totalTime = ((musicBean.getTotalPosMs() ?: 0) / 1000).toInt()
            curTime = ((musicBean.getCurrentPosMs() ?: 0) / 1000).toInt()

            musicName = ByteString.copyFrom(musicBean.getMusicName()?.toByteArray())
            singerName = ByteString.copyFrom(musicBean.getSingerName()?.toByteArray())

            val maxVolume = mMediaController?.playbackInfo?.maxVolume ?: 1
            volume = getVolume() * (100 / maxVolume)
        }
        CreekManager.sInstance.setMusic(model = operate, success = {

        }, failure = {_, m ->

        })

    }

    fun clearMusicInfoSendToCreekWatch() {
        laseMusicBean = null
        mMediaController = null
        sendMusicInfoToWatch(MusicBean("", "", "", 0, 0, 2, false, 0))
    }

    fun raiseVolume() {

        mAudioManager?.adjustStreamVolume(3, 1, AudioManager.FLAG_PLAY_SOUND)
        getMusicInfoSendToCreekWatch()
    }

    ///音乐转换成map
    private fun musicInfoToMap(musicBean: MusicBean): HashMap<String, Any?> {

        val mapTemp = HashMap<String, Any?>()
        mapTemp["singerName"] = musicBean.getSingerName()
        mapTemp["musicStatus"] = musicBean.getPlayState()
        mapTemp["playStatus"] = musicBean.isPlaying()
        mapTemp["musicName"] = musicBean.getMusicName()
        mapTemp["musicPackageName"] = musicBean.getMusicPackageName()
        mapTemp["currentVolume"] = musicBean.getCurrentVolume()
        mapTemp["totalTime"] = (musicBean.getTotalPosMs() ?: 0) / 1000
        mapTemp["curTime"] = (musicBean.getCurrentPosMs() ?: 0) / 1000
        return mapTemp

    }

    fun getMusicInfoToMap(): HashMap<String, Any?>? {
        val musicBean: MusicBean? = getMusicInfo()
        laseMusicBean = musicBean
        if (musicBean != null) {
            return musicInfoToMap(musicBean)
        }

        return null
    }

    private fun getMusicInfo(): MusicBean? {
        mMediaController?.metadata ?: return null

        val metadata: MediaMetadata = mMediaController!!.metadata ?: return null

        val playbackState: PlaybackState? = mMediaController?.playbackState
        val playbackInfo: MediaController.PlaybackInfo? = mMediaController?.playbackInfo
        var isPlaying = false
        var playbackStateTemp = 2
        if (playbackState != null && playbackState.state == 3) {
            isPlaying = true
            playbackStateTemp = playbackState.state
        }

        val currentVolume: Int = mAudioManager?.getStreamVolume(3) ?: 0
        val musicName = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
        val singerName =
            metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
        val albumArtistName =
            metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
        val albumName = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM)
        val duration: Long = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
        val packageName = mMediaController?.packageName
        val currentPosMs = mMediaController?.playbackState?.position ?: 0
        Log.d(TAG, "---------------------------------")
        Log.d(TAG, "| musicName: $musicName")
        Log.d(TAG, "| singerName: $singerName")
        Log.d(TAG, "| albumName: $albumName")
        Log.d(TAG, "| currentPosMs: $currentPosMs")
        Log.d(TAG, "| duration: $duration")
        Log.d(TAG, "| currentVolume: $currentVolume")
        Log.d(TAG, "| musicStatus: $playbackStateTemp")
        Log.d(TAG, "| playbackInfo?.maxVolume: ${playbackInfo?.maxVolume}")
        Log.d(TAG, "| playbackInfo?.currentVolume: ${playbackInfo?.currentVolume}")
        Log.d(TAG, "---------------------------------")
        return MusicBean(
            musicName,
            packageName,
            singerName,
            currentPosMs,
            duration,
            playbackStateTemp,
            isPlaying,
            currentVolume
        )

    }

    fun removeMediaController(packageName: String) {
        linkedHashMaps.remove(packageName)
    }

    private inner class VolumeChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e(TAG, "VolumeChangeReceiver.onReceive-${intent?.action}")
            if (intent?.action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                Log.e(TAG, "系统音量有变化--")
                this@CreekMediaControllerUtils.getMusicInfoSendToCreekWatch()
            }

        }

    }


    fun initMediaSessionManager(context: Context, audioManager: AudioManager, noticeServiceClassName: String) {
        Log.e(TAG, "initMediaSessionManager")
        setAudioManager(audioManager)
        setContext(context)
        clearMediaControllerCallback()
        val mediaSessionManager = context.getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        val localComponentName = ComponentName(context, noticeServiceClassName)
        mediaSessionManager.addOnActiveSessionsChangedListener(object :
            OnActiveSessionsChangedListener {
            override fun onActiveSessionsChanged(controllers: List<MediaController>?) {
                synchronized(this) {
                    if (controllers != null)
                        registerSessionCallback(controllers)
                }
            }
        }, localComponentName)
        synchronized(this) {
            val mActiveSessions = mediaSessionManager.getActiveSessions(localComponentName)
            registerSessionCallback(mActiveSessions)
        }

        registerVolumeReceiver()
        isActivityMediaSessionManager = true
    }


    private fun registerSessionCallback(mActiveSessions: List<MediaController>) {
        for (controller in mActiveSessions) {
            val sessionCallbacks = SessionCallbacks(controller)
            if (!this.linkedHashMaps.containsKey(controller.packageName)) {
                linkedHashMaps[controller.packageName] = sessionCallbacks
                controller.registerCallback(sessionCallbacks)
            } else {
                if (linkedHashMaps[controller.packageName] != null) {
                    controller.unregisterCallback(linkedHashMaps[controller.packageName]!!)
                    linkedHashMaps.remove(controller.packageName)
                }
                linkedHashMaps[controller.packageName] = sessionCallbacks
                controller.registerCallback(sessionCallbacks)
            }
            mMediaController = controller
        }
    }

}



