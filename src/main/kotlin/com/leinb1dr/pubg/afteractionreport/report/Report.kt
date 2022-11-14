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

    val DBNOs: AnnotatedField<Int> = AnnotatedField(0,ReportAnnotation.NONE),
    val assists: AnnotatedField<Int> = AnnotatedField(0,ReportAnnotation.NONE),
    val damageDealt: AnnotatedField<Double> = AnnotatedField(0.0,ReportAnnotation.NONE),
    val deathType: String = "none",
    val headshotKills: AnnotatedField<Int> = AnnotatedField(0,ReportAnnotation.NONE),
    val name: String = "#PlayerUnknown",
    val kills: AnnotatedField<Int> = AnnotatedField(0,ReportAnnotation.NONE),
    val winPlace: Int = 0,
    val killPlace: Int = 0,
    val timeSurvived: Double = 0.0,
    val heals: Int = 0,
    val revives: Int = 0,
    val killStreaks: Int = 0,
    val longestKill: Double = 0.0,
    val rideDistance: Double = 0.0,
    val roadKills: Int = 0,
    val swimDistance: Double = 0.0,
    val teamKills: Int = 0,
    val vehicleDestroys: Int = 0,
    val walkDistance: Double = 0.0,
    val weaponsAcquired: Int = 0,

    )

data class AnnotatedField<T>(val value: T, val annotation: ReportAnnotation) {
    override fun toString(): String =
        when(value){
            is Double -> String.format("%.2f", value)
                else -> "$value"
        }
}