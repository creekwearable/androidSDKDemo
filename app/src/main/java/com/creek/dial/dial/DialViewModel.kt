package com.creek.dial.dial

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

class DialViewModel : ViewModel() {

    val circleDialSelected = mutableIntStateOf(0)
    val squareDialSelected = mutableIntStateOf(0)

    val circleDialList = arrayListOf("fun061101_03","fun_061211_05_2","act06_1201_03")
    val squareDialList = arrayListOf("acti06_2","func6_pink","casi001_01")

    fun chooseCircleDial(navController: NavHostController,index: Int) {
        Log.d("DialViewModel", "chooseCircleDial: index = $index")
        circleDialSelected.intValue = index
        val name = circleDialList[index]
        navController.navigate("customDial/$name/466/466/233")
    }

    fun chooseSquareDial(navController: NavHostController, index: Int) {
        Log.d("DialViewModel", "chooseSquareDial: index = $index")
        squareDialSelected.intValue = index
        val name = squareDialList[index]
        navController.navigate("customDial/$name/368/448/60")
    }

}