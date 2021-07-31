package src.main.kotlin

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import routes.registerUserRoutes

// TODO Database Integration with PostgreSQL and Exposed
// TODO Profile Picture SFW Authorization (manual per admin?)
// TODO MAL or similiar API to search for anime and ratings instead of just accepting anime name
// TODO Check for valid link to picture file for profile picture
// TODO Allow for MAL/Kitsu/... login to link profiles and automatically update anime list and user name changes

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    registerUserRoutes()
}
