package src.main.kotlin.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import src.main.kotlin.database.redis.GrammarDatabase

private val db = GrammarDatabase()

fun Route.getGrammar() {
    get("/japanese/grammar/{query}") {
        val query = call.parameters["query"]
        val entry = query?.let { it1 -> db.getGrammar(it1) }
        if (entry != null) {
            if (entry.query != "") {
                call.respond(entry)
            } else {
                call.respondText("Grammar point not found.", status = HttpStatusCode.NotFound)
            }
        }
    }
}
