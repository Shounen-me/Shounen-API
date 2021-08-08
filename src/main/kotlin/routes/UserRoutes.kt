package routes

import functionality.clientId
import functionality.getMALUrl
import functionality.syncMal
import models.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.models.ProfilePicture
import java.nio.file.Files
import java.nio.file.Path

private val authorized_token: List<String> = Files.readAllLines(Path.of("src/main/resources/secrets/authorized_keys.txt"))
private val db = DatabaseAccess()

fun Route.createUser() {
    post("/user/{token}/{discordID}/{discordName}") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val discordID = call.parameters["discordID"]
            val name = call.parameters["discordName"]
            if (discordID?.let { it1 -> db.getUser(it1).id } == "") {
                name?.let { it1 -> User(discordID, it1) }?.let { it2 -> db.postUser(it2) }
                call.respondText("User stored correctly.", status = HttpStatusCode.Created)
            } else {
                call.respondText("User already exists.", status = HttpStatusCode.Forbidden)
            }
        }
    }
}

fun Route.getUser() {
    get("/user/{token}/{wildcard}") { // WildCard = Discord ID or Name
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val id = call.parameters["wildcard"]
            val user = id?.let { it1 -> db.getUser(it1) }
            if (user != null) {
                if (user.id == "") call.respondText("User doesn't exist", status = HttpStatusCode.NotFound)
                else call.respond(user)
            }
        }
    }
}

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

fun Route.postProfilePicture() {
    post("/user/{token}/{discordID}/profile") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val picture = call.receive<ProfilePicture>().link
            val id = call.parameters["discordID"]
            val user = id?.let { it1 -> db.getUser(it1) }
            if (user != null && user.id != "") {
                db.postProfilePicture(id, picture)
                call.respondText("Profile picture was set successfully.", status = HttpStatusCode.OK)
            } else call.respondText("User does not exist.", status = HttpStatusCode.NotFound)
        }
    }

}

fun Route.deleteUser() {
    delete("/user/{token}/{discordID}") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val id = call.parameters["discordID"]
            val user = id?.let { it1 -> db.getUser(it1) }
            if (user != null && user.id != "") {
                db.deleteUser(id)
                call.respondText("Deletion was successful.", status = HttpStatusCode.OK)
            } else
                call.respondText("User does not exist.", status = HttpStatusCode.NotFound)
        }
    }
}

fun Route.syncInit() {
    get("/{discordID}/mal/sync/init") {
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
    get("/{discordID}/mal/sync/{verifier}/{callback}") {
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


fun Application.registerUserRoutes() {
    routing {
        createUser()
        getUser()
        postAnime()
        postProfilePicture()
        deleteUser()
    }
}