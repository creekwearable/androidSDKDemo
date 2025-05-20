package com.creek.dial.music

import android.app.Application
import android.net.Uri
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.model.CreekMusicModel
import com.example.mylibrary.CreekManager
import kotlin.math.log

class MusicUploadViewModel(application: Application) : AndroidViewModel(application) {

    // 用于存储歌手名和专辑名
    private val _singerName = mutableStateOf("Unknown Artist")
    val singerName: State<String> = _singerName

    private val _albumName = mutableStateOf("Unknown Album")
    val albumName: State<String> = _albumName

    private val _selectedFile = mutableStateOf<String?>(null)
    val selectedFile: State<String?> = _selectedFile

     var fileDta:ByteArray? = null
     var songName = ""
     var singer = ""

    // 选择文件后获取文件路径并解析音频元数据
    fun onFileSelected(uri: Uri) {
        _selectedFile.value = uri.toString()

        // 获取文件的数据流
        val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
        fileDta = inputStream?.readBytes()

        // 解析音频元数据（歌手和专辑名）
        val metadata = getAudioFileMetadata(uri)
        metadata?.let { (singer, album) ->
            _singerName.value = singer
            _albumName.value = album
        }
        inputStream?.close()
    }

    // 使用 MediaMetadataRetriever 来解析音频文件元数据
    private fun getAudioFileMetadata(uri: Uri): Pair<String, String>? {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(getApplication<Application>().applicationContext, uri)

            // 获取歌手和专辑名
            songName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
            singer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown Album"

            return songName to singer
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }

        return null
    }


    fun startUpload() {

        fileDta?.let {
            CreekManager.sInstance.uploadMusic(musicModel = CreekMusicModel(songName = songName, singer = singer,), fileData = it, uploadProgress = { progress: Int ->
              Log.w("uploadMusic",progress.toString())
            }, uploadSuccess = {
                Log.w("uploadMusic","success")
            }, uploadFailure = {code, msg ->
                Log.w("uploadMusic",msg)
            })
        }
    }
}