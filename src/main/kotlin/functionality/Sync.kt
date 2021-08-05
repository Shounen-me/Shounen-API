package functionality

import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.functionality.generateCodeChallenge
import src.main.kotlin.functionality.generateCodeVerifier
import kotlin.random.Random


val db = DatabaseAccess()

// Syncing MAL account with shounen.me
fun sync_mal(discordID: String, userName: String ) {

}

// Fetch current anime list from MAL
fun fetch_list(discordID: String) {

}


// Fetch 'watching anime' (only last 10 results are shown as to not bloat the embed sent on Discord)
fun fetch_watching(discordID: String) {

}

fun getMALUrl(): String {
    val clientId = generateCodeVerifier()
    val codeChallenge = generateCodeChallenge(clientId)
    val id = Random(100)
    return "https://myanimelist.net/v1/oauth2/authorize?response_type=code&client_id=$clientId&code_challenge=$codeChallenge&state=RequestID$id"
}