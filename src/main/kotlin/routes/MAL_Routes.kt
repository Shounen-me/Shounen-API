package src.main.kotlin.routes

import functionality.callbackDiscord
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
var current_id = ""

fun Route.syncInit() {
    get("/mal/{discordID}/sync/init") {
        val mal = getMALUrl()
        val verifier = mal[0]
        val link = mal[1]
        current_id = call.parameters["discordID"]!!
        call.respondText("$verifier, $link", status = HttpStatusCode.OK)
    }
}

fun Route.syncRedirect() {
    get("/mal/redirect/{verifier}/{requestID}") {
        val verifier = call.parameters["verifier"]!!
        val requestID = call.parameters["requestID"]!!
        val discordID = call.parameters["discordID"]!!
        db.setVerifier(discordID, verifier)
        call.respondRedirect("https://myanimelist.net/v1/oauth2/authorize?response_type=code&client_id=$clientId&code_challenge=$verifier&state=RequestID$requestID")
    }
}

// https://api.shounen.me/mal/sync/standard?code=def50200fb9aaea44f48f7ecf281a0385d152550fd9ab026e707f038f158dd8034d58a0e7c595a8e060fe5049b3e6f1bd0d131f4b8d027c244829b0747007b95941bf9c1072e944e797596031d88fe04c6c5a7f8fdd6d2c243d165810298d0366630fc9f6f3b9a7232ea3032976f6fc3697dcdf8c2c96b9103381a6f44a90ce803f66d3ebd37a7183a6cca806f8f59fa6218b1d073b27c4fb43b8008ebd54a16408a7814f290c0062e7ad335f817100a2a3f895e886bf1b5db177304bcc9ab518244e6f8346e16e63f1755b77eadc6ee78554fe2ea3c46b07e185aae6344e6a1c4edc5f05b40d62f89befcd37c7e508300bfa80900a291464b644d40330c4bd1eda104eeb4c6b87aa71b5fa0ebb2d1c4b95a45b2f2dda3c09ce74c533a19607d8251820927bfc3a77cb69ffea5be5ec48edb788326d69fe605a36c922130c562c86401cf29f0622a21d7a9afbefc7b3783cfd808e7bb10fe6ef1880804027af42c6c9a694efa8c3ca5767d9ce1cd54437df9a20bdcc9cbe0e6ca23bdceecfabb185f54dc0f23a06ed24cb2ceb2f8258e1173c86550f7e8d9d143090505e5b983bf9e4ef63679173dad51758cb14cf2fc8db4db964fe958f78c62e40d11730602a419f9de0f409b63953db44188f311ebce2429f3dad26e3e484e7df47f109663758eb5923d2817f664&state=RequestID191
fun Route.syncCallbackStandard() {
    get("/mal/sync/standard{callback}") {
        val callback = call.parameters["callback"]
        if (callback != null) callbackDiscord(callback, current_id)
        call.respondText("Sync to Discord in process. Please wait a second.", status = HttpStatusCode.OK)
    }
}

fun Route.syncCallbackDiscord() {
    get("/mal/{discordID}/sync/discord/{verifier}/{callback}") {
        val code = call.parameters["callback"]!!
        val id = call.parameters["discordID"]!!
        val verifier = call.parameters["verifier"]!!
        syncMal(id, code, verifier)
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

fun Application.registerMALRoutes() {
    routing {
        postAnime()
        syncInit()
        syncRedirect()
        syncCallbackStandard()
        syncCallbackDiscord()
    }
}