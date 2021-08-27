package sync

import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.sync.MAL_Client
import src.main.kotlin.utils.generateVerifier
import src.main.kotlin.utils.Urls
import java.nio.file.Files
import java.nio.file.Path

val db = DatabaseAccess()


fun getRedirectURL(): String {
    val verifier = generateVerifier(128)
    val id = (0..1000).random()
    return "${Urls.redirectUri}$verifier/$id"
}

// Function for testing purposes after the MAL API callback
fun random(discordID: String) {
    println(db.getAccess(discordID))
    println(MAL_Client(discordID).getUserName())
}

