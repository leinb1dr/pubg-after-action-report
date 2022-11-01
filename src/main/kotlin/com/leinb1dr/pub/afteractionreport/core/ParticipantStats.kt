package com.leinb1dr.pub.afteractionreport.core

data class ParticipantStats(
    val DBNOs: Int,
    val assists: Int,
    val damageDealt: Double,
    val boosts: Int,
    val deathType: String = "none",
    val headshotKills: Int,
    val heals: Int,
    val killPlace: Int,
    val killStreaks: Int,
    val kills: Int,
    val longestKill: Double,
    val name: String = "#PlayerUnknown",
    val playerId: String = "#PlayerUnknown",
    val revives: Int,
    val rideDistance: Double,
    val roadKills: Int,
    val swimDistance: Double,
    val teamKills: Int,
    val timeSurvived: Int,
    val vehicleDestroys: Int,
    val walkDistance: Double,
    val weaponsAcquired: Int,
    val winPlace: Int
)
