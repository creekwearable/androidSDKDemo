package com.creek.dial
import com.example.model.SportModel


interface AssetColors {
    val healthZoneZone1: Int
    val healthZoneZone2: Int
    val healthZoneZone3: Int
    val healthZoneZone4: Int
    val healthZoneZone5: Int
}

class HeartRateProcessor(val colors: AssetColors) {

    var hrValueList: List<Int> = emptyList()
        private set
    var hrColorList: List<Int> = emptyList()
        private set
    var percentageList: List<Double> = emptyList()
        private set

    /**
     * Process heart-rate data to produce:
     * 1) hrColorList: color for each segment
     * 2) percentageList: end position of each segment (0–1), with the first value fixed at 0
     *
     * @param hrZoneList If empty, use the default [106, 124, 142, 159, 177].
     * Note: To stay consistent with the original Dart logic, compare zones[1] through zones[4] and ignore zones[0].
     */
    fun handleMapHeartRate(
        workoutDetail: SportModel,
        hrZoneList: List<Int>? = null
    ) {
        val hrValues = (workoutDetail.hrValueItem ?: emptyList()).filter { it != 0 }
        if (hrValues.isEmpty()) return

        val zones = hrZoneList ?: listOf(106, 124, 142, 159, 177)

        val colorsOut = mutableListOf<Int>()
        val percentagesOut = mutableListOf(0.0)

        var prevColor = getZoneColor(hrValues.first(), zones)
        colorsOut.add(prevColor)

        for (i in 1 until hrValues.size) {
            val color = getZoneColor(hrValues[i], zones)
            if (color != prevColor || i == hrValues.lastIndex) {
                val pct = i.toDouble() / (hrValues.lastIndex.toDouble())
                percentagesOut.add(pct)
                colorsOut.add(color)
                prevColor = color
            }
        }
        hrValueList = hrValues
        hrColorList = colorsOut
        percentageList = percentagesOut
    }

    private fun getZoneColor(heartRate: Int, zones: List<Int>): Int {
        return when {
            heartRate <= zones[1] -> colors.healthZoneZone1
            heartRate <= zones[2] -> colors.healthZoneZone2
            heartRate <= zones[3] -> colors.healthZoneZone3
            heartRate <= zones[4] -> colors.healthZoneZone4
            else -> colors.healthZoneZone5
        }
    }
}


fun hexToColorInt(hex: String): Int {
    val h = hex.removePrefix("#")
    return when (h.length) {
        6 -> (0xFF shl 24) or h.toInt(16)          // #RRGGBB
        8 -> h.toUInt(16).toInt()                  // #AARRGGBB
        else -> throw IllegalArgumentException("Bad color: $hex")
    }
}

fun main() {
    val assetColors = object : AssetColors {
        override val healthZoneZone1 = hexToColorInt("#00FF00")
        override val healthZoneZone2 = hexToColorInt("#99FF00")
        override val healthZoneZone3 = hexToColorInt("#FFFF00")
        override val healthZoneZone4 = hexToColorInt("#FF9900")
        override val healthZoneZone5 = hexToColorInt("#FF0000")
    }
    val processor = HeartRateProcessor(assetColors)
    val sportModel = SportModel()
    sportModel.hrValueItem = listOf(119,118,116,122,125,124,122,117,
        113,112,112,112,112,114,116,118,118,120,119,118,119,121,121,
        124,124,123,124,120,116,117,118,117,121,127,127,124,128,128,
        127,127,126,126,126,126,126,127,127,128,118,119,124,125,120,
        120,121,125,126,128,129,125,124,121,116,116,116,117,117,117,
        117,117,117,119,119,120,122,124,127,127,128,127,127,128,129,
        115,117,116,120,124,126,127,129,129,130,130,130,130,129,128,
        127,127,127,127,127,127,128,129,130,131,132,133,133,134,135,
        135,135,134,134,134,132,131,131,132,131,132,133,133,134,133,
        134,134,134,133,133,133,133,134,134,134,134,135,135,135,135,
        136,136,136,136,137,137,137,136,137,123,133,133,133,133,133,
        134,134,133,132,131,130,130,129,128,127,126,125,124,123,124,
        124,125,124,124,122,116,114,115,116,118,119,122,123,124,124,
        124,124,124,124,118,115,114,120,120,116,113,117,111,110,114,
        115,115,115,116,116,116,116,117,115,113,113,113,113,114,114,
        115,112,112,113,110,111,112,111,111,112,113,113,115,116,117,
        116,115,111,112,110,114,113,113,114,112,113,115,117,117,117,
        117,117,118,117,112,110,115,117,117,118,118,114,110,108,110,
        111,111,111,112,114,114,116,117,118,119,120,120,120,120,120,
        120,119,120,121,119,115,113,111,111,111,114,115,116,117,118,
        118,118,118,118,119,118,118,119,119,116,115,118,118,118,113,
        110,112,114,115,115,110,111,111,113,113,113,113,114,114,111,
        114,111,114,114,114,112,111,114,112,111,113,114,111,114,115,
        111,110,113,111,111,111,113,116,119,122,123,123,123,123,123,
        123,123,123,124,124,124,124,124,123,123,118,109,109,114,117,
        116,110,110,109,110,112,113,113,113,114,116,117,118,116,111,
        111,115,116,117,117,117,118,118,118,113,118,118,118,112,116,
        116,117,117,117,118,118,118,113,110,113,116,115,116)
    processor.handleMapHeartRate(
        workoutDetail = sportModel
    )
    println("Heart rate value list: ${processor.hrValueList}")
    println("Color list: ${processor.hrColorList}")
    println("Percentage breakpoints: ${processor.percentageList}")
    println(assetColors.healthZoneZone1)
    println(assetColors.healthZoneZone2)
    println(assetColors.healthZoneZone3)
    println(assetColors.healthZoneZone4)
    println(assetColors.healthZoneZone5)
}