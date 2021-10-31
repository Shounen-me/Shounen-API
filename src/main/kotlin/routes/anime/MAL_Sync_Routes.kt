package src.main.kotlin.routes

import sync.getRedirectURL
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import src.main.kotlin.database.postgres.DatabaseAccess
import src.main.kotlin.utils.SecretUtils.clientId
import src.main.kotlin.utils.Urls
import sync.random


private val db = DatabaseAccess()
var current_id = ""

fun Route.syncInit() {
    get("/mal/sync/{discordID}/init") {
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

fun Application.registerMALRoutes() {
    routing {
        syncInit()
        syncRedirect()
        syncCallbackStandard()
    }
}