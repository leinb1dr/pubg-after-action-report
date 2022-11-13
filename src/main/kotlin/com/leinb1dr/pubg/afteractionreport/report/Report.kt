package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.PubgMap

data class Report(
    val map: PubgMap,
    val time: String,
    val playerName: String = "#PlayerUnknown",
    val fields: ReportFields,
)

enum class ReportAnnotation(val emoji: String) {
    ABOVE("<:helm:415709362397642753>"), EVEN(":heavy_equals_sign:"), BELOW(""), NONE("")
}

data class ReportFields(

    val DBNOs: AnnotatedField<Int>,
    val assists: AnnotatedField<Int>,
    val damageDealt: AnnotatedField<Double>,
    val deathType: String = "none",
    val headshotKills: AnnotatedField<Int>,
    val name: String = "#PlayerUnknown",
    val kills: AnnotatedField<Int>,
    val winPlace: Int,
    val killPlace: Int,
    val timeSurvived: Double,
    val heals: Int,
    val revives: Int,
    val killStreaks: Int,
    val longestKill: Double,
//    val playerId: String = "#PlayerUnknown",
    val rideDistance: Double,
    val roadKills: Int,
    val swimDistance: Double,
    val teamKills: Int,
    val vehicleDestroys: Int,
    val walkDistance: Double,
    val weaponsAcquired: Int,

)

data class AnnotatedField<T>(val value: T, val annotation: ReportAnnotation) {
    override fun toString(): String =
        when(value){
            is Double -> String.format("%.2f", value)
                else -> "$value"
        }
}