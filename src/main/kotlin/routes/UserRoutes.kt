package routes

import models.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import src.main.kotlin.models.ProfilePicture
import java.nio.file.Files
import java.nio.file.Path

private val authorized_token: List<String> = Files.readAllLines(Path.of("src/main/resources/authorized_keys.txt"))

fun Route.createUser() {
    post("/user/{token}/{discordID}/{discordName}") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        }
        if (userStorage.find { it.id == call.parameters["discordID"] } == null) {
            val user = call.parameters["discordName"]?.let { it1 -> User(call.parameters["discordID"], it1) }
            if (user != null) {
                userStorage.add(user)
            }
            call.respondText("User stored correctly", status = HttpStatusCode.Created)
        }  else {
            call.respondText("User already exists", status = HttpStatusCode.Forbidden)
        }
    }
}

fun Route.getUser() {
    get("/user/{token}/{discordID}") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val user = userStorage.find { it.id == call.parameters["discordID"] }
            if (user == null) call.respondText("User doesn't exist", status = HttpStatusCode.NotFound)
            else call.respond(user)
        }
    }
}


fun Route.userSearch() {
    get("/user/{token}/search/{wildcard}") { // WildCard = Discord ID or Name
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val user: User?
            val searchParameter: String? = call.parameters["wildcard"]
            if (searchParameter != null && searchParameter.toLongOrNull() == null) {
                user = userStorage.find { it.userName == searchParameter }
            } else {
                user = userStorage.find { it.id == searchParameter }
            }

            if (user == null) call.respondText("User does not exist.", status = HttpStatusCode.NotFound)
            else call.respond(user)
        }
    }
}

fun Route.postAnime() {
    post("/user/{token}/{discordID}/anime/{name}") {
        if (!authorized_token.contains(call.parameters["token"])) {
            call.respondText("Unauthorized access.", status = HttpStatusCode.Unauthorized)
        } else {
            val user = userStorage.find { it.id == call.parameters["discordID"] }
            if (user != null) {
                user.addAnime(call.parameters["name"])
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
            val user = userStorage.find { it.id == call.parameters["discordID"] }
            if (user != null) {
                user.setProfilePicture(picture)
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
            if (userStorage.removeIf { it.id == call.parameters["discordID"] })
                call.respondText("Deletion was successful.", status = HttpStatusCode.OK)
            else
                call.respondText("User does not exist.", status = HttpStatusCode.NotFound)
        }
    }
}


fun Application.registerUserRoutes() {
    routing {
        createUser()
        getUser()
        userSearch()
        postAnime()
        postProfilePicture()
        deleteUser()
    }
}