package routes.anime

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import src.main.kotlin.models.anime.PostQuery
import src.main.kotlin.sync.MAL_Client
import src.main.kotlin.utils.SecretUtils.authorized_token

// http://localhost:8080/mal/anime/mfeWxfHNFQ/166883258200621056/44074/11
fun Route.postAnime() {
    post("/mal/anime/{token}/{discordID}/{id}/{episodes}") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val query =
                PostQuery(
                    call.parameters["id"]!!.toLong(),
                    call.parameters["discordID"]!!,
                    call.parameters["episodes"]!!.toInt(),
                )
            MAL_Client(query.discordID).postAnime(query.queryID, query.count)
            call.respondText("Anime added successfully.", status = HttpStatusCode.OK)
        }
    }
}

fun Route.postManga() {
    post("/mal/manga/{token}/{discordID}/{id}/{chapters}") {
        if (authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val query =
                PostQuery(
                    call.parameters["id"]!!.toLong(),
                    call.parameters["discordID"]!!,
                    call.parameters["episodes"]!!.toInt(),
                )
            if (query.count != 0) {
                MAL_Client(query.discordID).postManga(query.queryID, query.count)
            } else {
                MAL_Client(query.discordID).postManga(query.queryID)
            }
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
