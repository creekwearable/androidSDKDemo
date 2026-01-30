package com.creek.dial.music

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.model.CourseModel
import com.example.mylibrary.CreekManager
import com.example.mylibrary.sportType

class RouteUploadViewModel(application: Application) : AndroidViewModel(application) {


    private val _selectedFile = mutableStateOf<String?>(null)
    val selectedFile: State<String?> = _selectedFile
    var fileData:ByteArray? = null



    fun onFileSelected(uri: Uri) {
        val context = getApplication<Application>().applicationContext
        val fileName = getFileName(context, uri)
        _selectedFile.value = fileName
        val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
        fileData = inputStream?.readBytes()
        inputStream?.close()
    }


    fun getFileName(context: Context, uri: Uri): String {
        var name = ""
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }

    fun startUpload() {

        fileData?.let{
            CreekManager.sInstance.getGPXEncodeUint8List(data = it, geoId = 85563801595, sportType = sportType.IRUN,model = {
                    lat: Double, lng: Double ->
                return@getGPXEncodeUint8List "shanghai"
            }, encode = {
                    encodeData: ByteArray ->
                CreekManager.sInstance.upLoadGeo(data = encodeData, geoId = 85563801595, uploadProgress = { progress: Int ->
                    Log.w("upLoadGeo",progress.toString())
                }, uploadSuccess = {
                    Log.w("upLoadGeo","success")
                }, uploadFailure = {code, msg ->
                    Log.w("upLoadGeo",msg)
                })

            })
        }
    }
}