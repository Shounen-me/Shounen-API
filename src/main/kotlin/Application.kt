package src.main.kotlin

import routes.registerCustomerRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*

// TODO Database Integration with PostgreSQL
// TODO More tests -> Testing every single functionality
// TODO Profile Picture SFW Authorization
// TODO MAL or similiar API to search for anime and ratings instead of just accepting anime name

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    registerCustomerRoutes()
}
