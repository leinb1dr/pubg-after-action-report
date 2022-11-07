package com.leinb1dr.pub.afteractionreport.report

data class Report(
    val map: String,
    val time: String,
    val fields: ReportFields
)

enum class ReportAnnotation(val emoji: String){
    ABOVE(":chicken:"), EVEN(":heavy_equals_sign:"), BELOW(":potato:"), NONE("")
}

data class ReportFields(

    val DBNOs: Int,
    val assists: Int,
    val damageDealt: Double,
    val damageDealtAnnotation: ReportAnnotation,
    val deathType: String = "none",
    val headshotKills: Int,
    val name: String = "#PlayerUnknown",
    val kills: Int,
    val winPlace: Int,
    val heals: Int,
//    val killPlace: Int,
//    val killStreaks: Int,
//    val longestKill: Double,
//    val playerId: String = "#PlayerUnknown",
//    val revives: Int,
//    val rideDistance: Double,
//    val roadKills: Int,
//    val swimDistance: Double,
//    val teamKills: Int,
//    val timeSurvived: Int,
//    val vehicleDestroys: Int,
//    val walkDistance: Double,
//    val weaponsAcquired: Int,

)
