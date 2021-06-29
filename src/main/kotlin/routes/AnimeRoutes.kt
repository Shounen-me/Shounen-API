package routes

import models.animeStorage
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.listAnimes() {
    get("/anime") {
        if (animeStorage.isNotEmpty()) {
            call.respond(animeStorage)
        }
    }
}

fun Route.getAnime() {
    get("/anime/{id}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val anime = animeStorage.find { it.id == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(anime)
    }
}

fun Route.getAnimeElement() {
    get("/anime/{id}/{element}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val anime = animeStorage.find { it.id == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        val element = anime.contains(call.parameters["element"])
            ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        call.respondText { element }
    }
}

fun Application.registerAnimeRoutes() {
    routing {
        listAnimes()
        getAnime()
        getAnimeElement()
    }
}