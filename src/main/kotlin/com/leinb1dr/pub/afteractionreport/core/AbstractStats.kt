package com.leinb1dr.pub.afteractionreport.core

abstract class AbstractStats {
    abstract val DBNOs: Int
    abstract val assists: Int
    abstract val damageDealt: Double
    abstract val boosts: Int
    abstract val headshotKills: Int
    abstract val heals: Int
    abstract val kills: Int
    abstract val longestKill: Double
    abstract val revives: Int
    abstract val rideDistance: Double
    abstract val roadKills: Int
    abstract val swimDistance: Double
    abstract val teamKills: Int
    abstract val timeSurvived: Int
    abstract val vehicleDestroys: Int
    abstract val walkDistance: Double
    abstract val weaponsAcquired: Int
}