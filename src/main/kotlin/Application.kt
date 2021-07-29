package src.main.kotlin

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import routes.registerUserRoutes

// TODO Database Integration with PostgreSQL
// TODO More tests -> Testing every single functionality
// TODO Profile Picture SFW Authorization
// TODO MAL or similiar API to search for anime and ratings instead of just accepting anime name
// TODO Only authorize API calls for authorized Bots via environment variable/text file
// TODO Check for valid link to picture file for profile picture

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    registerUserRoutes()
}
