package com.maquina

import kotlinx.serialization.Serializable


@Serializable
data class Maquina(
    val id: String,
    val machine_tag: String,
    val start_time: String,
    var end_time: String,
    var reason: String
    )

val maquinaStorage = mutableListOf<Maquina>()
