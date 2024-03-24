package com.creek.dial.customDial

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.model.DialParseModel
import com.example.mylibrary.CreekManager
import com.example.proto.Enums
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.Base64
import java.util.zip.ZipInputStream


class CustomDialViewModel : ViewModel() {
    
    

    val colorSelectedIndex = mutableIntStateOf(0)
    val backgroundSelectedIndex = mutableIntStateOf(0)
    val positionSelectedIndex = mutableIntStateOf(0)
    val functionSelectedIndex = mutableIntStateOf(0)
    var baseImage = mutableStateOf("")


    var width = 0
    var height = 0
    var cornerRadius = 0
    var titleName = ""

     val _dialModel = MutableLiveData(DialParseModel(null,null,null,null,null,null))
     val dialModel: LiveData<DialParseModel> = _dialModel
    fun updateDialModel(newDialModel: DialParseModel) {
        _dialModel.value = newDialModel
        baseImage.value = newDialModel.previewImageBytes ?: ""

    }


    fun installDial() {
        Log.d("CustomDialViewModel", "installDial")
        CreekManager.sInstance.encodeDial { model ->
            val decimalArray: IntArray =
                model.map { it.toInt() and 0xFF }.toIntArray()
            CreekManager.sInstance.upload(fileName = "$titleName.bin", fileData = decimalArray, uploadProgress = {
                Log.d("dial", "dial progress = $it")
            }, uploadSuccess = {

            }, uploadFailure = {c,m ->

            })
        }
    }

    fun chooseColor(index: Int) {
        Log.d("CustomDialViewModel", "chooseColor: index = $index")

        CreekManager.sInstance.setCurrentColor(selectIndex = index, model = {

            updateDialModel(it)
            colorSelectedIndex.intValue = index
        })

    }

    fun chooseBackground(index: Int) {
        Log.d("CustomDialViewModel", "chooseBackground: index = $index")
        backgroundSelectedIndex.intValue = index
        CreekManager.sInstance.setCurrentBackgroundImagePath(selectIndex = index, model = {
            updateDialModel(it)
        })
    }

    fun choosePosition(index: Int) {
        Log.d("CustomDialViewModel", "choosePosition: index = $index")
        positionSelectedIndex.intValue = index
    }

    fun chooseFunction(index: Int) {
        Log.d("CustomDialViewModel", "chooseFunction: index = $index")
        functionSelectedIndex.intValue = index
        var i = 0
        var selectIndexs:MutableList<Int> = mutableListOf()
        dialModel.value?.functions?.forEach {item ->
            if (positionSelectedIndex.intValue == i){
                selectIndexs.add(functionSelectedIndex.intValue)
            }else{
                selectIndexs.add(item.selectedIndex ?: 0)
            }
            i++
        }
        CreekManager.sInstance.setCurrentFunction(selectIndex = selectIndexs, model = {model->
            updateDialModel(model)

        })
    }

    fun customDial() {
        Log.d("CustomDialViewModel", "customDial")
    }

    @Composable
    fun unzipFile(titleName: String, width: Int, height: Int, cornerRadius: Int) {
        val context = LocalContext.current
        try {
            val inputStream = context.assets.open("$titleName.zip")
            unzipFileFromStream(context,inputStream, titleName,width, height, cornerRadius)
        } catch (e: Exception) {
            e.printStackTrace()
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
            val current = File("${destinationDir.path}/${entry.name}")
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
        CreekManager.sInstance.parseDial(path = destinationDir.path, height = height, width = width, radius = cornerRadius,
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
}