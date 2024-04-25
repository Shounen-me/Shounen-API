package routes.anime

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import src.main.kotlin.database.postgres.DatabaseAccess
import src.main.kotlin.utils.SecretUtils.clientId
import src.main.kotlin.utils.Urls
import sync.getRedirectURL
import sync.random

private val db = DatabaseAccess()
var currentId = ""

fun Route.syncInit() {
    get("/mal/sync/{discordID}/init") {
        val link = getRedirectURL()
        currentId = call.parameters["discordID"]!!
        call.respondText(link, status = HttpStatusCode.OK)
    }
}

fun Route.syncRedirect() {
    get("/mal/redirect/{verifier}/{requestID}") {
        val verifier = call.parameters["verifier"]!!
        val requestID = call.parameters["requestID"]!!
        db.setVerifier(currentId, verifier)
        call.respondRedirect(
            "${Urls.oauthBaseUrl}authorize?response_type=code&client_id=$clientId&code_challenge=$verifier&state=RequestID$requestID",
        )
    }
}

fun Route.syncCallbackStandard() {
    get("/mal/sync/standard") {
        val code = call.request.queryParameters["code"]!!
        db.setCode(currentId, code)
        random("166883258200621056")
        call.respondText("Sync to Discord in process. Please return to Discord.", status = HttpStatusCode.OK)
    }
}

fun Application.registerMALRoutes() {
    routing {
        syncInit()
        syncRedirect()
        syncCallbackStandard()
    }
}
