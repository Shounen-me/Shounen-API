package src.main.kotlin

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import routes.registerUserRoutes
import src.main.kotlin.routes.registerMALRoutes

// TODO Profile Picture SFW Authorization (manual per admin?)
// TODO Check for valid link to picture file for profile picture
// TODO Check why the heck I get a bad request response from MAL

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    registerUserRoutes()
    registerMALRoutes()
}
