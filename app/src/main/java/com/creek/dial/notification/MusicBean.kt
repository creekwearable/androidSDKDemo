package com.notification_listener_util.data
///音量信息
class MusicBean internal constructor(
    private var musicName: String?,
    private var musicPackageName: String?,
    private var singerName: String?,
    private var currentPosMs: Long?,
    private var totalPosMs: Long?,
    private var playState: Int?,
    private var isPlaying: Boolean,
    private var currentVolume: Int?,
) {

    fun getPlayState(): Int? {
        return playState
    }

    fun getMusicPackageName(): String? {
        return musicPackageName
    }

    fun setMusicPackageName(packageName: String?) {
        musicPackageName = packageName
    }

    fun getMusicName(): String? {
        return musicName
    }


    fun setMusicName(str: String?) {
        musicName = str
    }

    fun getSingerName(): String? {
        return singerName
    }

    fun setSingerName(str: String?) {
        singerName = str
    }

    fun getCurrentPosMs(): Long? {
        return currentPosMs
    }

    fun setCurrentPosMs(currentPosMs: Long) {
        this.currentPosMs = currentPosMs
    }

    fun getTotalPosMs(): Long? {
        return totalPosMs
    }

    fun setTotalPosMs(totalPosMs: Long) {
        this.totalPosMs = totalPosMs
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }

    fun setPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
    }

    fun getCurrentVolume(): Int? {
        return currentVolume
    }

    override fun toString(): String {
        return "MusicBean(musicName=$musicName, musicPackageName=$musicPackageName, singerName=$singerName, currentPosMs=$currentPosMs, totalPosMs=$totalPosMs, playState=$playState, isPlaying=$isPlaying, currentVolume=$currentVolume)"
    }


}