package com.dolphinbaytech.islandhopper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

data class Vessel(
    var name: String = "",
    var departTerminal: String = "",
    var atDock: Boolean = false,
    var arriveTerminal: String = "",
    var scheduledDeparture: LocalDateTime = LocalDateTime.MIN,
    var actualDeparture: LocalDateTime = LocalDateTime.MIN,
    var estimatedArrival: LocalDateTime = LocalDateTime.MIN
)

data class Schedule(
    var vesselName: String = "",
    var departTime: LocalDateTime = LocalDateTime.MIN,
    var arriveTime: LocalDateTime = LocalDateTime.MIN,
    var duration: Long = 0
)

class MainViewModel : ViewModel() {
    var depart by mutableStateOf(value = Orcas)
    var arrive by mutableStateOf(value = Anacortes)

    val todayMillis: Long = initTodayMilli()
    var dateMillis by mutableLongStateOf(value = todayMillis)

    var vesselList: MutableList<Vessel> = mutableStateListOf()
    var scheduleList: MutableList<Schedule> = mutableStateListOf()

    fun initTodayMilli() : Long {
        val zonedDateTime: ZonedDateTime = Instant.now().atZone(ZoneId.of("UTC"))
        val startOfDayInstant: Instant = zonedDateTime.truncatedTo(ChronoUnit.DAYS).toInstant()
        return startOfDayInstant.toEpochMilli()
    }

    fun prevDay() {
        dateMillis -= 24 * 60 * 60 * 1000
    }

    fun nextDay() {
        dateMillis += 24 * 60 * 60 * 1000
    }

    fun setTerminals(depart: Terminal, arrive: Terminal) {
        this.depart = depart
        this.arrive = arrive
    }

    fun vesselExists(vesselName: String) : Boolean {
        for (schedule in scheduleList) {
            if (vesselName == schedule.vesselName) return true
        }
        return false
    }
}
