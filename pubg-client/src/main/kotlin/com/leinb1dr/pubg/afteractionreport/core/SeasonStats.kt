package com.leinb1dr.pubg.afteractionreport.core

import com.fasterxml.jackson.annotation.JsonProperty

data class SeasonStats(
    override val assists: Int = 0,
    override val boosts: Int = 0,
    @JsonProperty("dBNOs")
    override val DBNOs: Int = 0,
    val dailyKills: Int = 0,
    val dailyWins: Int = 0,
    override val damageDealt: Double = 0.0,
    val days: Int = 0,
    override val headshotKills: Int = 0,
    override val heals: Int = 0,
    val killPoints: Double = 0.0,
    override val kills: Int = 0,
    override val longestKill: Double = 0.0,
    val longestTimeSurvived: Double = 0.0,
    val losses: Int = 0,
    val maxKillStreaks: Int = 0,
    val mostSurvivalTime: Int = 0,
    val rankPoints: Double = 0.0,
    val rankPointsTitle: String = "",
    override val revives: Int = 0,
    override val rideDistance: Double = 0.0,
    override val roadKills: Int = 0,
    val roundMostKills: Int = 0,
    val roundsPlayed: Int = 1,
    val suicides: Int = 0,
    override val swimDistance: Double = 0.0,
    override val teamKills: Int = 0,
    override val timeSurvived: Int = 0,
    val top10s: Int = 0,
    override val vehicleDestroys: Int = 0,
    override val walkDistance: Double = 0.0,
    override val weaponsAcquired: Int = 0,
    val weeklyKills: Int = 0,
    val weeklyWins: Int = 0,
    val winPoints: Double = 0.0,
    val wins: Int = 0
) : AbstractStats()
