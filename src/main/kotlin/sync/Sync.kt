package sync

import com.google.gson.Gson
import kotlinx.serialization.Serializable
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.create
import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.utils.LoginService
import src.main.kotlin.utils.generateVerifier
import src.main.kotlin.models.Token
import src.main.kotlin.utils.Urls
import java.nio.file.Files
import java.nio.file.Path


@Serializable
data class AnimeList(val data: Array<String>, val paging: String)

val db = DatabaseAccess()
val clientId: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[0]
val clientSecret: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[1]
val client = OkHttpClient()

// Syncing MAL account with shounen.me

fun syncMal(discordID: String, code: String, verifier: String ) {

    val login = createLoginService()
    val response = login.getAccessToken(clientId, clientSecret, code, verifier, "authorization_code").execute()
    if (response.isSuccessful) {
        val token = response.body()
        println(token!!.access_token)
        // db.syncMalToDB(discordID, token)
    }
}

// Refresh a user's access token
fun refreshToken(refresh_token: String)  {

}

fun getRedirectURL(): List<String> {
    val verifier = generateVerifier(128)
    val id = (0..1000).random()
    val link = "${Urls.redirectUri}$verifier/$id"
    return mutableListOf(verifier, link)
}

fun createLoginService(): LoginService =
        Retrofit.Builder()
        .baseUrl(Urls.oauthBaseUrl)
        .build().create(LoginService::class.java)

