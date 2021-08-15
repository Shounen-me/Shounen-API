package sync

import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.models.Token
import src.main.kotlin.utils.LoginService
import src.main.kotlin.utils.generateVerifier
import src.main.kotlin.utils.Urls
import java.nio.file.Files
import java.nio.file.Path



@Serializable
data class AnimeList(val data: Array<String>, val paging: String)

val db = DatabaseAccess()
val clientId: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[0]
val clientSecret: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[1]
lateinit var token: Token
val client = OkHttpClient()

// Syncing MAL account with shounen.me

fun syncMal(discordID: String, code: String, verifier: String ) {

    val login = createLoginService()
    val call : Call<Token> = login.getAccessToken(clientId, clientSecret, code, verifier,"authorization_code")
    call.enqueue(object : Callback<Token> {

        override fun onResponse(call: Call<Token>, response: Response<Token>) {
            println(response.message())
            println(response.code())
            if (response.isSuccessful && response.body() != null) {
                token = response.body()!!
            }
            else {
                println("Token was null")
            }
        }

        override fun onFailure(call: Call<Token>, t: Throwable) {
            println("Error: $t")
        }
    })
    println("Test")
    println(token.access_token)
}

// Refresh a user's access token
fun refreshToken(refresh_token: String)  {

}

fun getRedirectURL(): String {
    val verifier = generateVerifier(128)
    val id = (0..1000).random()
    return "${Urls.redirectUri}$verifier/$id"
}

fun createLoginService(): LoginService =
    Retrofit.Builder()
        .baseUrl(Urls.oauthBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(LoginService::class.java)

