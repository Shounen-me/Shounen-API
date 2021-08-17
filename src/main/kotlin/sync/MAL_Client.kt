package src.main.kotlin.sync

import com.kttdevelopment.mal4j.MyAnimeList
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator
import src.main.kotlin.database.DatabaseAccess
import sync.clientId
import sync.clientSecret

class MAL_Client(discordID: String) {

    val access = DatabaseAccess().getAccess(discordID)
    private val mal: MyAnimeList =
        MyAnimeList.withAuthorization(MyAnimeListAuthenticator(clientId, clientSecret, access.code, access.verifier))

    fun getUserName() : String = mal.authenticatedUser.name
}