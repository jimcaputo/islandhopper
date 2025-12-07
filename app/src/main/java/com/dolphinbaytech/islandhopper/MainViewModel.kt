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
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class Vessel(
    var name: String = "",
    var departTerminal: String = "",
    var atDock: Boolean = false,
    var arriveTerminal: String = "",
    var scheduledDeparture: LocalDateTime = LocalDateTime.now(),
    var actualDeparture: LocalDateTime = LocalDateTime.now(),
    var estimatedArrival: LocalDateTime = LocalDateTime.now()
)

data class Schedule(
    var vesselName: String = "",
    var departTime: LocalDateTime = LocalDateTime.now(),
    var arriveTime: LocalDateTime = LocalDateTime.now(),
    var duration: Long = 0
)

class MainViewModel : ViewModel() {
    var depart by mutableStateOf(value = Orcas)
    var arrive by mutableStateOf(value = Anacortes)

    val todayMillis: Long = initTodayMilli()
    val todayDateTime: LocalDateTime = initTodayDateTime()
    var dateMillis by mutableLongStateOf(value = todayMillis)

    var vesselList: MutableList<Vessel> = mutableStateListOf()
    var scheduleList: MutableList<Schedule> = mutableStateListOf()

    fun initTodayMilli() : Long {
        val zonedDateTime: ZonedDateTime = Instant.now().atZone(ZoneId.systemDefault())
        val startOfDayInstant: Instant = zonedDateTime.truncatedTo(ChronoUnit.DAYS).toInstant()
        return startOfDayInstant.toEpochMilli()
    }

    fun initTodayDateTime() : LocalDateTime {
        val instant: Instant = Instant.ofEpochMilli(todayMillis)
        val localDateTime: LocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localDateTime
    }

    fun getFormattedDate() : String {
        val instant: Instant = Instant.ofEpochMilli(dateMillis)
        val localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate()
        return localDate.format(DateTimeFormatter.ofPattern("EEE MM/dd/yy"))
    }

    fun showDay(utcMilli: Long) : Boolean {
        val instant: Instant = Instant.ofEpochMilli(utcMilli)
        val localDateTime: LocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        return localDateTime >= todayDateTime
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
}

object IslandHopper {
    lateinit var mvm: MainViewModel
    var reqId: Long = 0

    fun create(mvm: MainViewModel) {
        this.mvm = mvm
    }

    fun updateSchedules() {
        reqId++
        FerryAPI.fetchSchedules(reqId)
    }
}