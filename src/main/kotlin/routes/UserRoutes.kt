package routes

import models.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.userRouting() {

    // Post new profile or get an existing one
    route("/user/{discordID}") {

        // Post new user
        post("{discordName}") {
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

        // Add anime
        post("{anime}") {
            val user = userStorage.find { it.id == call.parameters["discordID"] }
            if (user == null) call.respondText("User doesn't exist", status = HttpStatusCode.NotFound)
            else {
                user.addAnime(call.parameters["anime"])
                call.respondText("Anime added successfully", status = HttpStatusCode.OK)
            }
        }

        // Set profile picture
        post("{profilePicture") {
            val user = userStorage.find { it.id == call.parameters["discordID"] }
            if (user == null) call.respondText("User doesn't exist", status = HttpStatusCode.NotFound)
            else {
                call.parameters["profilePicture"]?.let { it1 -> user.setProfilePicture(it1) }
                call.respondText("Profile picture was set correctly", status = HttpStatusCode.OK)
            }
        }


        // Get user's own profile
        get() {
            val user = userStorage.find { it.id == call.parameters["discordID"] }
            if (user == null) call.respondText("User doesn't exist", status = HttpStatusCode.NotFound)
            else call.respond(user)
        }

        // Get user profile by Discord name
        get("{discordName") {
            val user = userStorage.find { it.userName == call.parameters["discordName"] }
            if (user == null) call.respondText("User doesn't exist or changed name", status = HttpStatusCode.NotFound)
            else call.respond(user)
        }

        // Get user profile by unique Discord User ID
        get("searchedID") {
            val user = userStorage.find { it.id == call.parameters["searchedID"] }
            if (user == null) call.respondText("User doesn't exist", status = HttpStatusCode.NotFound)
            else call.respond(user)
        }

        // Delete user profile
        delete() {
            val user = userStorage.find { it.id == call.parameters["discordID"] }
            if (user == null) call.respondText("User doesn't exist", status = HttpStatusCode.NotFound)
            else {
                userStorage.remove(user)
                call.respondText("User successfully deleted", status = HttpStatusCode.OK)
            }
        }
    }


}


fun Application.registerCustomerRoutes() {
    routing {
        userRouting()
    }
}