package src.main.kotlin.routes

import functionality.clientId
import functionality.getMALUrl
import functionality.syncMal
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import src.main.kotlin.database.DatabaseAccess
import java.nio.file.Files
import java.nio.file.Path

private val authorized_token: List<String> = Files.readAllLines(Path.of("src/main/resources/secrets/authorized_keys.txt"))
private val db = DatabaseAccess()

fun Route.syncInit() {
    get("/mal/{discordID}/sync/init") {
        val mal = getMALUrl()
        call.respondText("Verifier: $mal[0]" + " & " + "Link: $mal[1] " + call.parameters["discordID"], status = HttpStatusCode.OK)
    }
}

fun Route.syncRedirect() {
    get("/mal/redirect/{verifier}/{requestID}") {
        val verifier = call.parameters["verifier"]!!
        val requestID = call.parameters["requestID"]!!
        call.respondRedirect("https://myanimelist.net/v1/oauth2/authorize?response_type=code&client_id=$clientId&code_challenge=$verifier&state=RequestID$requestID")
    }
}

fun Route.syncCallbackStandard() {
    get("/mal/sync/{callback}") {
        call.parameters["callback"]?.let { it1 -> call.respondText(it1, status = HttpStatusCode.OK) }
    }
}

fun Route.syncCallbackDiscord() {
    get("/mal/{discordID}/sync/{verifier}/{callback}") {
        val param = call.parameters["callback"]
        if (param != null) {
            val s = param.substring(param.indexOf("?") + 1)
            val parameters = s.split("&")
            val code = parameters[0].substring(5)
            val id = call.parameters["discordID"]!!
            val verifier = call.parameters["verifier"]!!
            syncMal(id, code, verifier)
        }
    }
}

// Still need to change this from animeID to the actual anime name for a better user experience
fun Route.postAnime() {
    post("/mal/{token}/{discordID}/anime/{animeID}") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val id = call.parameters["discordID"]
            val anime = call.parameters["name"]
            /*
            // Replace '+' seperated name to get the actual name
            if (anime != null && anime.contains("+")) anime = anime.replace("+", " ")
             */
            val user = id?.let { it1 -> db.getUser(it1) }
            if (user != null && anime != null && user.id != "") {
                if (db.postAnime(id, anime))
                    call.respondText("Anime added successfully.", status = HttpStatusCode.OK)
                else
                    call.respondText("Anime couldn't be added, please try again..", status = HttpStatusCode.NotFound)
            } else {
                call.respondText("User does not exist.", status = HttpStatusCode.NotFound)
            }
        }
    }
}

fun Application.registerUserRoutes() {
    routing {
        postAnime()
        syncInit()
        syncRedirect()
        syncCallbackStandard()
        syncCallbackDiscord()
    }
}