package src.main.kotlin

import routes.registerCustomerRoutes
import routes.registerAnimeRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*

// TODO Database Integration with PostgreSQL
// TODO More tests -> Testing every single functionality
//

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    registerCustomerRoutes()
    registerAnimeRoutes()
}
