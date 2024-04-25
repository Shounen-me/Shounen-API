package src.main.kotlin

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import routes.anime.registerMALQueryRoutes
import routes.anime.registerMALRoutes
import routes.anime.registerUserRoutes
import src.main.kotlin.database.postgres.DatabaseSingleton

// High Priority:
// TODO: Dockerization + Docker Compose
// TODO: Increase Code style (awful amount of code smells from younger me)
// TODO: Increase Code coverage and use KoTest (with TestContainers)
// TODO: Move plugins to separate libraries.gradle.kts
// TODO: Retake shounen.me domain and deploy project and move to cloud
// TODO: Move to Microservice architecture
// TODO: Additional features

// Low Priority:
// TODO Profile Picture SFW Authorization
// TODO Check for valid link to picture file for profile picture

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    DatabaseSingleton.connect(environment.config)

    registerUserRoutes()
    registerMALRoutes()
    registerMALQueryRoutes()
}
