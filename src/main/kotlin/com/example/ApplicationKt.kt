package com.example

import com.example.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        routing {
            homeRoute()
        }
    }.start(wait = true)
}
