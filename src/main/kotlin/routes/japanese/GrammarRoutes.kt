package src.main.kotlin.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import src.main.kotlin.database.DatabaseAccess_JP

private val db = DatabaseAccess_JP()

fun Route.getGrammar() {

    get("/japanese/grammar/{query}") {
        val query = call.parameters["query"]
        val entry = query?.let { it1 -> db.getGrammar(it1) }
        if (entry != null) {
            if (entry.query != "")
                call.respond(entry)
            else
                call.respondText("Grammar point not found.", status = HttpStatusCode.NotFound)
        }

    }

}