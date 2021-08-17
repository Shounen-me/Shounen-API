package src.main.kotlin.routes

import sync.clientId
import sync.getRedirectURL
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.utils.Urls
import sync.random
import java.nio.file.Files
import java.nio.file.Path

private val authorized_token: List<String> = Files.readAllLines(Path.of("src/main/resources/secrets/authorized_keys.txt"))
private val db = DatabaseAccess()
var current_id = ""

// http://localhost:8080/mal/166883258200621056/sync/init
fun Route.syncInit() {
    get("/mal/{discordID}/sync/init") {
        val link = getRedirectURL()
        current_id = call.parameters["discordID"]!!
        call.respondText(link, status = HttpStatusCode.OK)
    }
}

fun Route.syncRedirect() {
    get("/mal/redirect/{verifier}/{requestID}") {
        val verifier = call.parameters["verifier"]!!
        val requestID = call.parameters["requestID"]!!
        db.setVerifier(current_id, verifier)
        call.respondRedirect("${Urls.oauthBaseUrl}authorize?response_type=code&client_id=$clientId&code_challenge=$verifier&state=RequestID$requestID")
    }
}

fun Route.syncCallbackStandard() {
    get("/mal/sync/standard") {
        val code = call.request.queryParameters["code"]!!
        db.setCode(current_id, code)
        random("166883258200621056")
        call.respondText("Sync to Discord in process. Please return to Discord.", status = HttpStatusCode.OK)
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
                    call.respondText("Anime couldn't be added, please try again.", status = HttpStatusCode.NotFound)
            } else {
                call.respondText("User does not exist.", status = HttpStatusCode.NotFound)
            }
        }
    }
}

fun Application.registerMALRoutes() {
    routing {
        postAnime()
        syncInit()
        syncRedirect()
        syncCallbackStandard()
    }
}