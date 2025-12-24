package com.dolphinbaytech.islandhopper

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object IslandHopper {
    lateinit var mvm: MainViewModel
    var reqId: Long = 0

    fun create(mvm: MainViewModel) {
        this.mvm = mvm
    }

    fun updateSchedules() {
        reqId++
        mvm.scheduleList.clear()
        FerryAPI.fetchSchedules(reqId)

        if (mvm.dateMillis == mvm.todayMillis) {
            FerryAPI.fetchVessels()
        }
    }

    fun getLocalDateTime(timeMillis: Long, timeZone: String) : LocalDateTime {
        val instant: Instant = Instant.ofEpochMilli(timeMillis)
        val localDateTime: LocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(timeZone))
        return localDateTime
    }

    fun getFormattedDate(dateMillis: Long, timeZone: String) : String {
        val localDateTime = getLocalDateTime(timeMillis = dateMillis, timeZone = timeZone)
        return localDateTime.format(DateTimeFormatter.ofPattern("EEE MM/dd/yy"))
    }

    fun getFormattedTime(localDateTime: LocalDateTime) : String {
        var time = localDateTime.format(DateTimeFormatter.ofPattern("h:mm"))
        time += if (localDateTime.hour < 12) "am" else "pm"
        return time
    }
}