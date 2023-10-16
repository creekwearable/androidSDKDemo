package com.example.androidsdkdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mylibrary.CreekManager
import com.example.proto.Enums
import java.io.IOException


class MainActivity : AppCompatActivity() {

    val listCmd: List<String> = listOf(
        "Binding", "Get Device Information", "Sync", "Upload", "Get Device Bluetooth Status",
        "Get Language", "Set Language", "Sync Time", "Get Time", "Get User Information",
        "Set User Information", "Get Alarm Clock", "Set Alarm Clock", "Get Do Not Disturb",
        "Set Do Not Disturb", "Get Screen Brightness", "Set Screen Brightness",
        "Get Health Monitoring", "Health monitoring setting", "Sleep monitoring acquisition",
        "Sleep monitoring setting", "World clock acquisition", "World clock setting",
        "Message switch query", "Message switch setting", "Set weather",
        "Incoming call configuration query", "Incoming call configuration settings",
        "Contacts query", "Contacts settings", "Exercise self-identification query",
        "Exercise self-identification settings", "Exercise sub-item data query",
        "Exercise sub-item data setting", "Inquiry about the arrangement order of device exercise",
        "Setting the arrangement order of device exercise",
        "Get the type of exercise supported by the device", "Setting the heart rate interval",
        "Delete the dial", "Query the dial", "Set the dial", "System operation",
        "Query activity data", "Query sleep data", "Query heart rate data", "Query pressure data",
        "Query noise data", "Query blood oxygen data", "Exercise record list",
        "Query exercise details", "Range query exercise record", "Delete exercise record"
    )

    private val REQUEST_BLUETOOTH_PERMISSION = 1
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "操作平台"
        setContentView(R.layout.activity_main)
        CreekManager.sInstance.register(application)
        CreekManager.sInstance.initSDK()
        var rightTitle =findViewById<TextView>(R.id.rightTitle)
        var centerTitle =findViewById<TextView>(R.id.centerTitle)
        var listView =findViewById<ListView>(R.id.listview)
        var addpter = ArrayAdapter(this,android.R.layout.simple_list_item_1,listCmd)

        listView!!.adapter = addpter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 处理项点击事件
            val selectedItem = listCmd[position]
            when (selectedItem) {
                "Sync" -> {
                    CreekManager.sInstance.sync(syncSuccess = {
                        Log.w("Sync","syncSuccess")
                    }, syncFailure = {
                        Log.w("Sync","syncFailure")
                    }, syncProgress = {
                            progress: Int ->
                        Log.w("Sync","$progress")
                    })

                }
                "Upload" -> {
                    Thread{
                        try {
                            val inputStream = assets.open("res.ota") // "file.txt" 是 assert 目录下的文件名
                            // 使用 inputStream 处理资源数据
                            var  fileData :ByteArray  = inputStream.readBytes()
                            CreekManager.sInstance.upload("res.ota", fileData, uploadProgress = {
                                    progress ->
                                print(progress)
                            }, uploadSuccess = {}, uploadFailure = {c,m ->})
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }.start()

                }
                "Binding" -> {
                    CreekManager.sInstance.bindingDevice(bindType = Enums.bind_method.BIND_NORMAL,id = null, code = null, success = {

                    }, failure = {})
                }
                else -> {
                    val intent = Intent(this, SendCwdActivity::class.java)
                    intent.putExtra("name",selectedItem)
                    startActivity(intent)
                }
            }

        }
        rightTitle.text="Bluetooth"
        centerTitle.text="operating platform"
        rightTitle.setOnClickListener {
            requestBluetoothPermissions()
            // 检查是否已经获得蓝牙权限
            if (hasBluetoothPermissions()) {
                // 已经获得了蓝牙权限，可以执行相关操作
                startActivity(Intent(this,ScanActivity::class.java))
            } else {
                // 申请蓝牙权限
                requestBluetoothPermissions()
            }

        }

    }


    private fun requestBluetoothPermissions() {
        val permissions = arrayOf(
           // Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_MEDIA_LOCATION

        )

        ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了蓝牙权限，可以执行相关操作
                startActivity(Intent(this,ScanActivity::class.java))
            } else {
                // 用户拒绝了蓝牙权限，可以显示一条提示信息或执行其他操作
              //  Toast.makeText(this, "拒绝了蓝牙权限所以不能扫描设备", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun hasBluetoothPermissions(): Boolean {
        //compileSdkVersion项目中编译SDK版本大于30申请以下权限可使用
        //Manifest.permission.BLUETOOTH_SCAN、Manifest.permission.BLUETOOTH_ADVERTISE、Manifest.permission.BLUETOOTH_CONNECT
        //若小于30可以直接使用权限对应的字符串
        if (Build.VERSION.SDK_INT > 30) {
            if ((ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_SCAN") != PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this, "android.permission.BLUETOOTH_ADVERTISE")
                        != PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this, "android.permission.ACCESS_COARSE_LOCATION")
                        != PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_CONNECT") != PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        "android.permission.BLUETOOTH_SCAN",
                        "android.permission.BLUETOOTH_ADVERTISE",
                        "android.permission.BLUETOOTH_CONNECT"
                    ), REQUEST_BLUETOOTH_PERMISSION
                )
                return false
            }
        }
        return true
    }
}