package src.main.kotlin.sync

import com.kttdevelopment.mal4j.MyAnimeList
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator
import com.kttdevelopment.mal4j.anime.Anime
import com.kttdevelopment.mal4j.manga.Manga
import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.utils.SecretUtils.clientId
import src.main.kotlin.utils.SecretUtils.clientSecret

class MAL_Client(discordID: String) {

    private val access = DatabaseAccess().getAccess(discordID)
    private val mal: MyAnimeList =
        MyAnimeList.withAuthorization(MyAnimeListAuthenticator(clientId, clientSecret, access.code, access.verifier))

    fun getUserName() : String = mal.authenticatedUser.name

    fun postAnime(animeID: Long) = mal.updateAnimeListing(animeID).update()
    fun postAnime(animeID: Long, episodes: Int) = mal.updateAnimeListing(animeID).episodesWatched(episodes).update()

    fun postManga(mangaID: Long) = mal.updateMangaListing(mangaID).update()
    fun postManga(mangaID: Long, chapters: Int) = mal.updateMangaListing(mangaID).chaptersRead(chapters).update()

    fun getAnime(id: Long) : Anime = mal.getAnime(id)

    fun getManga(id: Long) : Manga = mal.getManga(id)

}