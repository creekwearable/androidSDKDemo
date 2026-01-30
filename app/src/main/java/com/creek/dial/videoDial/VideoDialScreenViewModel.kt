package com.creek.dial.videoDial

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.model.DialParseModel
import com.example.model.DialVideoParseModel
import com.example.mylibrary.CreekManager
import com.example.proto.Enums
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Base64
import java.util.zip.ZipInputStream


class VideoDialViewModel : ViewModel() {

    val colorSelectedIndex = mutableIntStateOf(0)
    val positionSelectedIndex = mutableIntStateOf(0)
    var baseImage = mutableStateOf("")
    var width = 0
    var height = 0
    var cornerRadius = 0
    var titleName = ""
     val _dialModel = MutableLiveData(DialVideoParseModel(videoPath = null, previewImageBytes = null, videoSelectIndex = null))
     val dialModel: LiveData<DialVideoParseModel> = _dialModel
    var playVideoPath =  mutableStateOf("")
    var uptateVideValue =  mutableIntStateOf(0)
    var saveVideoPath: String = ""



    fun updateDialModel(newDialModel: DialVideoParseModel) {
        _dialModel.value = newDialModel
        playVideoPath.value = newDialModel.videoPath ?: ""
        baseImage.value = newDialModel.previewImageBytes ?: ""
        colorSelectedIndex.intValue = newDialModel.videoSelectIndex ?: 0
        uptateVideValue.intValue += 1
    }


    fun installDial() {
        Log.d("CustomDialViewModel", "installDial")
        CreekManager.sInstance.encodeVideoDial { model ->
            CreekManager.sInstance.uploadNew(fileName = "video.bin", fileData = model, uploadProgress = {
                Log.d("dial", "dial progress = $it")
            }, uploadSuccess = {

            }, uploadFailure = {c,m ->

            })
        }
    }

    fun chooseColor(index: Int) {
        Log.d("CustomDialViewModel", "chooseColor: index = $index")
        CreekManager.sInstance.setCurrentVideoColor(selectIndex = index, model = {

            updateDialModel(it)
            colorSelectedIndex.intValue = index
        })

    }

    fun choosePosition(index: Int) {
        Log.d("CustomDialViewModel", "choosePosition: index = $index")
        CreekManager.sInstance.setCurrentVideoClockPosition(selectIndex = index, model = {
            updateDialModel(it)
            positionSelectedIndex.intValue = index
        })
    }


    @Composable
    fun unzipFile(titleName: String, width: Int, height: Int, cornerRadius: Int) {
        val context = LocalContext.current
        val fileManager = context.filesDir
        val destinationDir = File(fileManager, "directory")
        val current = File("${destinationDir.path}/${titleName}")
        if(current.exists()){
            parseDial(context = context,titleName=titleName,width=width,height=height,cornerRadius=cornerRadius)
        }else{
            try {
                val inputStream = context.assets.open("$titleName.zip")
                unzipFileFromStream(context,inputStream, titleName,width, height, cornerRadius)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun unzipFileFromStream(context: Context, inputStream: InputStream, titleName: String,width:Int,height:Int,cornerRadius:Int) {
        val fileManager = context.filesDir
        val destinationDir = File(fileManager, "directory")
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }
        val zis = ZipInputStream(BufferedInputStream(inputStream))
        var entry = zis.nextEntry
        while(entry != null){
            var current = File("${destinationDir.path}/${entry.name}")
            if (!current.path.contains(titleName)){
                current = File("${destinationDir.path}/${titleName}/${entry.name}")
            }
            if(entry.isDirectory){
                current.mkdirs()
            }else{
                current.parentFile?.mkdirs()
                zis.buffered().copyTo(current.outputStream())
            }
            entry = zis.nextEntry
        }

        parseDial(context = context,titleName=titleName,width=width,height=height,cornerRadius=cornerRadius)
    }

    fun parseDial(context: Context,titleName: String,width:Int,height:Int,cornerRadius:Int){
        val fileManager = context.filesDir
        val destinationDir = File(fileManager, "directory/$titleName")
        CreekManager.sInstance.parseVideoDial(path = destinationDir.path, height = height, width = width, radius = cornerRadius,
            platformType = Enums.Platform.JX_3085C_PLATFORM
        ){ model->
            updateDialModel(model)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadImageFromBase64(base64String: String): Bitmap? {
        val imageBytes: ByteArray = Base64.getDecoder().decode(base64String)
        val inputStream: InputStream = ByteArrayInputStream(imageBytes)
        return BitmapFactory.decodeStream(inputStream)
    }

    // 获取视频第一帧
    fun getFirstFrameFromVideo(context: Context, uri: Uri): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val bitmap = retriever.getFrameAtTime(0)
            retriever.release()
            bitmap
        } catch (e: Exception) {
            null
        }
    }




    // 保存裁剪参数
    fun saveCropRect(rect: Rect?) {
        // 你可以将裁剪参数保存到 ViewModel 或上传到服务器
        rect?.let {
            Log.d("VideoDialViewModel", "Crop rect: x=${it.left}, y=${it.top}, w=${it.width()}, h=${it.height()}")
            CreekManager.sInstance.setVideoDial(videoPath = saveVideoPath, startSecond = 0, endSecond = 5, cropW = it.width().toDouble(), cropH = it.height()
                .toDouble(),
                cropX = it.left.toDouble(), cropY = it.top.toDouble(), model = {
                    videoUrl ->
                    CreekManager.sInstance.setCurrentVideoColor(selectIndex = 0, model = {
                        updateDialModel(it)
                    })
                }, failure = {
                    code, message ->
            })
        }
    }

    fun saveVideoToLocal(context: Context, uri: Uri) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val fileExtension = when (contentResolver.getType(uri)) {
            "video/mp4" -> "mp4"
            "video/3gpp" -> "3gp"
            "video/webm" -> "webm"
            else -> "mp4"
        }

        val fileName = "video.$fileExtension"
        val destFile = File(context.filesDir, fileName)

        try {
            // 如果目标文件已存在，先删除
            if (destFile.exists()) {
                destFile.delete()
            }

            FileOutputStream(destFile).use { output ->
                inputStream.use { input ->
                    input.copyTo(output)
                }
            }
            // 设置全局路径变量
            saveVideoPath = destFile.absolutePath
            Log.d("VideoSave", "视频保存成功：$saveVideoPath")

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("VideoSave", "视频保存失败：${e.message}")
        }
    }

}