package functionality

import com.google.gson.Gson
import kotlinx.serialization.Serializable
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.functionality.generateVerifier
import java.nio.file.Files
import java.nio.file.Path

@Serializable
data class MAL_DATA(val token_type: String, val expires_in: String, val access_token: String, val refresh_token: String)

@Serializable
data class AnimeList(val data: Array<String>, val paging: String)

val db = DatabaseAccess()
val clientId: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[0]
val clientSecret: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[1]
val client = OkHttpClient()

// Syncing MAL account with shounen.me
fun syncMal(discordID: String, code: String, verifier: String ) {
    val requestBody = FormBody.Builder()
        .add("client_id", clientId)
        .add("client_secret", clientSecret)
        .add("code", code)
        .add("code_verifier", verifier)
        .add("grant_type", "authorization")
        .build()

    val request = Request.Builder()
        .url(" https://myanimelist.net/v1/oauth2/token")
        .post(requestBody)
        .build()

    val response = client.newCall(request).execute()
    val data = Gson().fromJson(response.body().string(), MAL_DATA::class.java)
    println(data)
    db.syncMalToDB(discordID, data.access_token, data.refresh_token)
}

// Refresh a user's access token
fun refreshToken(refresh_token: String): MAL_DATA {
    val requestBody = FormBody.Builder()
        .add("client_id", clientId)
        .add("client_secret", clientSecret)
        .add("grant_type", "refresh_token")
        .add("refresh_token", refresh_token)
        .build()

    val request = Request.Builder()
        .url(" https://myanimelist.net/v1/oauth2/token")
        .post(requestBody)
        .build()

    val response = client.newCall(request).execute()
    return Gson().fromJson(response.body().string(), MAL_DATA::class.java)
}

fun getRedirectURL(): List<String> {
    val verifier = generateVerifier(128)
    val id = (0..1000).random()
    val link = "https://api.shounen.me/mal/redirect/$verifier/$id"
    return mutableListOf(verifier, link)
}
