package src.main.kotlin.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import src.main.kotlin.sync.MAL_Client
import src.main.kotlin.utils.SecretUtils.authorized_token

fun Route.postAnime() {
    post("/mal/{token}/{discordID}/anime/{id}/{episodes}}") {
        if (authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val animeID = call.parameters["id"]!!
            val discordID = call.parameters["discordID"]!!
            if (call.parameters["episodes"] != null)
                call.parameters["episodes"]?.let { it1 -> MAL_Client(discordID).postAnime(animeID.toLong(), it1.toInt()) }
            else
                MAL_Client(discordID).postAnime(animeID.toLong())
            call.respondText("Anime added successfully.", status = HttpStatusCode.OK)
        }
    }
}

fun Route.postManga() {
    post("/mal/{token}/{discordID}/manga/{id}}") {
        if (authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val mangaID = call.parameters["id"]!!
            val discordID = call.parameters["discordID"]!!
            if (call.parameters["episodes"] != null)
                call.parameters["episodes"]?.let { it1 -> MAL_Client(discordID).postAnime(mangaID.toLong(), it1.toInt()) }
            else
                MAL_Client(discordID).postAnime(mangaID.toLong())
            call.respondText("Manga added successfully.", status = HttpStatusCode.OK)
        }
    }
}

fun Route.getAnimeAndManga() {
    get("/mal/{discordID}/{type}/{id}") {
        val type = call.parameters["type"]!!
        val id = call.parameters["id"]!!.toLong()
        val discordID = call.parameters["discordID"]!!
        if (type == "anime") {
            call.respond(MAL_Client(discordID).getAnime(id))
        } else if (type == "manga") {
            call.respond(MAL_Client(discordID).getManga(id))
        }
    }
}


fun Application.registerMALQueryRoutes() {
    routing {
        postAnime()
        postManga()
        getAnimeAndManga()
    }
}