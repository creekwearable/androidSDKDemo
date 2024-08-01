package com.creek.dial
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Period

fun predictedMenstrualPeriod(
    startMenstrualDate: LocalDate,
    cycle: Int,
    len: Int,
    closure: (startPeriod: LocalDate, endPeriod: LocalDate, startMaybePeriod: LocalDate, endMaybePeriod: LocalDate) -> Unit
) {
    // 预测下个月经开始时间
    val startPeriod = startMenstrualDate.plusDays(cycle.toLong())

    // 预测下个月经结束时间
    val endPeriod = startMenstrualDate.plusDays((cycle + len - 1).toLong())

    // 预测下个月经可能开始时间
    val startMaybePeriod = startMenstrualDate.plusDays((cycle - 2).toLong())

    // 预测下个月经可能结束时间
    val endMaybePeriod = startMenstrualDate.plusDays((cycle + len + 2 - 1).toLong())

    closure(startPeriod, endPeriod, startMaybePeriod, endMaybePeriod)
}

// 预测的易孕窗口期

fun maybeGestation(
    startMenstrualDate: LocalDate,
    cycle: Int,
    len: Int,
    closure: (startPeriod: LocalDate?, endPeriod: LocalDate?) -> Unit
) {
    val gestation = cycle - len

    // 孕期开始时间
    val startGestation: LocalDate?

    // 孕期结束时间
    val endGestation: LocalDate?
    when {
        gestation > 17 -> {
            startGestation = startMenstrualDate.plusDays((cycle - 14 - 3).toLong())
            endGestation = startMenstrualDate.plusDays((cycle - 14 + 2).toLong())
        }
        gestation == 15 -> {
            startGestation = startMenstrualDate.plusDays((cycle - 14).toLong())
            endGestation = startMenstrualDate.plusDays((cycle - 14 + 2).toLong())
        }
        gestation == 16 -> {
            startGestation = startMenstrualDate.plusDays((cycle - 14 - 1).toLong())
            endGestation = startMenstrualDate.plusDays((cycle - 14 + 2).toLong())
        }
        gestation == 17 -> {
            startGestation = startMenstrualDate.plusDays((cycle - 14 - 2).toLong())
            endGestation = startMenstrualDate.plusDays((cycle - 14 + 2).toLong())
        }
        else -> {
            startGestation = null
            endGestation = null
        }
    }
    closure(startGestation, endGestation)
}

// 主函数，用于执行测试

fun main() {
    // 测试预测生理期功能
    predictedMenstrualPeriod(
        startMenstrualDate = LocalDate.of(2024, 6, 26),
        cycle = 15,
        len = 8
    ) { startPeriod, endPeriod, startMaybePeriod, endMaybePeriod ->
        println("预测生理周期: ${startPeriod.toString().substring(0, 10)} - ${endPeriod.toString().substring(0, 10)}")
        println("可能生理周期: ${startMaybePeriod.toString().substring(0, 10)} - ${endMaybePeriod.toString().substring(0, 10)}")
    }

    // 测试预测易孕窗口期功能
    maybeGestation(
        startMenstrualDate = LocalDate.of(2024, 6, 26),
        cycle = 15,
        len = 8
    ) { startPeriod, endPeriod ->
        if (startPeriod == null && endPeriod == null) {
            println("没有孕期")
        } else {
            println("预测的孕期: ${startPeriod.toString().substring(0, 10)} - ${endPeriod.toString().substring(0, 10)}")
        }
    }
}