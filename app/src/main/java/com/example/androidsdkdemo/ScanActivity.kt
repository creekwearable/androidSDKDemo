package com.example.androidsdkdemo

import CustomAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidsdkdemo.R
import com.example.model.ScanDeviceModel
import com.example.mylibrary.CreekManager


class ScanActivity : AppCompatActivity() {

    private var listScanDeviceModel: List<ScanDeviceModel> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scan)
        CreekManager.sInstance.register(application)
        var centerTitle =findViewById<TextView>(R.id.centerTitle)
        var listView =findViewById<ListView>(R.id.listview)

        centerTitle.text="device"
        var iconBack =findViewById<ImageView>(R.id.icon_back)
        iconBack.setOnClickListener {
            finish()
        }

        CreekManager.sInstance.scan(timeOut = 15, devices = { model: Array<ScanDeviceModel> ->
            // 处理设备列表
            listScanDeviceModel= model.toList()
           // addpter.setDeviceModel(model.toList())
            var addpter = CustomAdapter(this,listScanDeviceModel)
            listView!!.adapter = addpter
        } ,endScan = {

        })
    }


    override fun onDestroy() {
        CreekManager.sInstance.stopScan()
        super.onDestroy()

    }

}