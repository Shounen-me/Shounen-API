package src.main.kotlin.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import src.main.kotlin.sync.MAL_Client
import src.main.kotlin.utils.SecretUtils.authorized_token

// http://localhost:8080/mal/anime/mfeWxfHNFQ/166883258200621056/44074/11
fun Route.postAnime() {
    post("/mal/anime/{token}/{discordID}/{id}/{episodes}") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val animeID = call.parameters["id"]!!
            val discordID = call.parameters["discordID"]!!
            val episodeCount = call.parameters["episodes"]!!.toInt()
            MAL_Client(discordID).postAnime(animeID.toLong(), episodeCount)
            call.respondText("Anime added successfully.", status = HttpStatusCode.OK)
        }
    }
}

fun Route.postManga() {
    post("/mal/manga/{token}/{discordID}/{id}/{volumes}/{chapters}") {
        if (authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val mangaID = call.parameters["id"]!!
            val discordID = call.parameters["discordID"]!!
            val chapterCount = call.parameters["chapters"]!!.toInt()
            val volumeCount = call.parameters["volumes"]!!.toInt()
            MAL_Client(discordID).postManga(mangaID.toLong(), volumeCount, chapterCount)
            call.respondText("Manga added successfully.", status = HttpStatusCode.OK)
        }
    }
}

fun Route.getAnimeAndManga() {
    get("/mal/info/{discordID}/{type}/{id}") {
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