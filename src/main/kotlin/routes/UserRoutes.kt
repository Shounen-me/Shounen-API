package routes

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
    post("/user/{token}/{discordID}/anime/{name}") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val id = call.parameters["discordID"]
            var anime = call.parameters["name"]
            // Replace '+' seperated name to get the actual name
            if (anime != null && anime.contains("+")) anime = anime.replace("+", " ")
            val user = id?.let { it1 -> db.getUser(it1) }
            if (user != null && anime != null && user.id != "") {
                db.postAnime(id, anime)
                call.respondText("Anime added successfully.", status = HttpStatusCode.OK)
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


fun Application.registerUserRoutes() {
    routing {
        createUser()
        getUser()
        postAnime()
        postProfilePicture()
        deleteUser()
    }
}