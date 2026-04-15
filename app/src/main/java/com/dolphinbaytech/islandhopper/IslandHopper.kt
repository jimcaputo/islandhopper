package com.dolphinbaytech.islandhopper

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
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

    fun getLocation(activity: MainActivity) {
        if (ContextCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { currentLocation: Location? ->
                    if (currentLocation != null) {
                        mvm.initTerminals(currentLocation)
                    }
                }
        } else {
            // In case the user has not enabled location services, just do an init with Anacortes location
            val location = Location("")
            location.latitude = Anacortes.lat
            location.longitude = Anacortes.long
            mvm.initTerminals(currentLocation = location)
        }
    }

    fun reset(activity: MainActivity) {
        mvm.initTodayMilli()
        getLocation(activity)
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