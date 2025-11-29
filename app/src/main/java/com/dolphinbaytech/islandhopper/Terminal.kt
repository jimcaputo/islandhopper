package com.dolphinbaytech.islandhopper

val Anacortes = Terminal(name = "Anacortes", id = 1, latLong = "48.502654, -122.679297")
val FridayHarbor = Terminal(name = "Friday Harbor", id = 10, latLong = "48.534236, -123.015236")
val Lopez = Terminal(name = "Lopez", id = 13, latLong = "48.570405, -122.883685")
val Orcas = Terminal(name = "Orcas", id= 15, latLong = "48.598541, -122.945827")
val Shaw = Terminal(name = "Shaw", id = 18, latLong = "48.584230, -122.929721")

val Terminals = listOf(Anacortes, FridayHarbor, Lopez, Orcas, Shaw)

class Terminal(var name: String, var id: Int, var latLong: String) {

    // HACK: optimized for just me to target Orcas :)
    fun getArrivalTerminal() : Terminal {
        if (name == Anacortes.name) return Orcas
        return Anacortes
    }
}
