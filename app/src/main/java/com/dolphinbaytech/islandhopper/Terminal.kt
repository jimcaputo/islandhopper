package com.dolphinbaytech.islandhopper

data class Terminal(
    var name: String,
    var id: Int,
    var lat: Double,
    var long: Double
)

val Anacortes = Terminal(name = "Anacortes", id = 1, lat = 48.502654, long = -122.679297)
val FridayHarbor = Terminal(name = "Friday Harbor", id = 10, lat = 48.534236, long = -123.015236)
val Lopez = Terminal(name = "Lopez", id = 13, lat = 48.570405, long = -122.883685)
val Orcas = Terminal(name = "Orcas", id= 15, lat = 48.598541, long = -122.945827)
val Shaw = Terminal(name = "Shaw", id = 18, lat = 48.584230, long = -122.929721)

val Terminals = listOf(Anacortes, FridayHarbor, Lopez, Orcas, Shaw)
