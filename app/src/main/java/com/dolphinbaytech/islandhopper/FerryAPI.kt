package com.dolphinbaytech.islandhopper

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


object FerryAPI {
    lateinit var queue: RequestQueue

    fun create(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    fun fetchSchedules(mvm: MainViewModel) {
        // If the user set the terminals both the same, then clear the ListView, and simply return.
        if (mvm.depart.name == mvm.arrive.name) return

        val instant: Instant = Instant.ofEpochMilli(mvm.dateMillis)
        val localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate()
        val urlParams = "${localDate.year}-${localDate.monthValue}-${localDate.dayOfMonth}/" +
                "${mvm.depart.id}/${mvm.arrive.id}?apiaccesscode=${ApiKeys.WSDOT}"

        val url = "https://www.wsdot.wa.gov/ferries/api/schedule/rest/schedule/$urlParams"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { jsonSchedules ->
                // TODO - do some date matching to make sure this response matters, maybe here, or maybe in the callback that's
                // populating the actual UI. This seems like it should be a better way than doing that wonky counter thing
                processSchedules(mvm, jsonSchedules)
            },
            { error ->
            })

        try {
            queue.add(request)
        } catch (error: Exception) {
        }
    }

    fun processSchedules(mvm: MainViewModel, jsonSchedules: JSONObject) {
        try {
            val schedules: JSONArray =
                jsonSchedules.getJSONArray("TerminalCombos").getJSONObject(0).getJSONArray("Times")

            for (i in 0..<schedules.length()) {
                val schedule = Schedule()
                val json = schedules.getJSONObject(i)

                schedule.vesselName = getJsonString(json, name = "VesselName")
                schedule.departTime = getJsonTime(json, name = "DepartingTime")
                schedule.arriveTime = getJsonTime(json, name = "ArrivingTime")
                schedule.duration = Duration.between(schedule.departTime, schedule.arriveTime).toMinutes()

                // If cal is today, then only show times later in the day (plus 1 hour buffer for ferries running late)
                if (mvm.dateMillis == mvm.todayMillis) {
                    val instant: Instant = Instant.now()
                    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("America/Los_Angeles"))

                    // Calculate the time between now and the departTime, positive or negative. If the departTime < -60,
                    // this means the ferry departs earlier than 1 hour ago and can be excluded from display our list.
                    val dur = Duration.between(localDateTime, schedule.departTime).toMinutes()
                    if (dur < -60) {
                        continue
                    }
                }
                mvm.scheduleList.add(schedule)
            }
        }
        catch (error: Exception) {
        }
    }

    fun fetchVessels(mvm: MainViewModel) {
        val url = "https://www.wsdot.wa.gov/ferries/api/vessels/rest/vessellocations?apiaccesscode=${ApiKeys.WSDOT}"

        val request = JsonArrayRequest(
            Request.Method.GET, url,null,
            { jsonVessels ->
                processVessels(mvm, jsonVessels)
            },
            { error ->
            }
        )

        try {
            queue.add(request)
        } catch (error: Exception) {
        }
    }

    fun processVessels(mvm: MainViewModel, jsonVessels: JSONArray) {
        try {
            mvm.vesselList.clear()
            for (i in 0..<jsonVessels.length()) {
                val jsonVessel: JSONObject = jsonVessels.getJSONObject(i)
                val vessel = Vessel()

                vessel.name = getJsonString(json = jsonVessel, name = "VesselName")
                vessel.departTerminal = getJsonString(json = jsonVessel, name = "DepartingTerminalName")
                vessel.scheduledDeparture = getJsonTime(json = jsonVessel, name = "ScheduledDeparture")

                if (!jsonVessel.has("AtDock")) break
                vessel.atDock = jsonVessel.getBoolean("AtDock")
                if (!vessel.atDock) {
                    vessel.arriveTerminal = getJsonString(json = jsonVessel, name = "ArrivingTerminalName")
                    vessel.actualDeparture = getJsonTime(json = jsonVessel, name = "LeftDock")
                    vessel.estimatedArrival = getJsonTime(json = jsonVessel, name = "Eta")
                }
                mvm.vesselList.add(vessel)
            }
        } catch (error: Exception) {
        }
    }

    fun getJsonString(json: JSONObject, name: String): String {
        try {
            if (json.has(name)) return json.getString(name)
        } catch (error: Exception) {
        }
        return ""
    }

    fun getJsonTime(json: JSONObject, name: String): LocalDateTime {
        var localDateTime: LocalDateTime
        var time = ""

        try {
            if (json.has(name)) time = json.getString(name)
        } catch (error: Exception) {

        }

        if (time.contains("-")) {
            val epochMilli = time.substring(time.indexOf("(") + 1, time.indexOf("-")).toLong()
            val instant: Instant = Instant.ofEpochMilli(epochMilli)
            localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("America/Los_Angeles"))
        } else {
            localDateTime = LocalDateTime.now()
        }
        return localDateTime
    }
}