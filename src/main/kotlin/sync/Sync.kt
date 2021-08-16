package sync

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.utils.LoginService
import src.main.kotlin.utils.generateVerifier
import src.main.kotlin.utils.Urls
import java.nio.file.Files
import java.nio.file.Path



val db = DatabaseAccess()
val clientId: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[0]
val clientSecret: String = Files.readAllLines(Path.of("src/main/resources/secrets/mal.txt"))[1]


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

