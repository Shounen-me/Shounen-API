package src.main.kotlin.sync

import com.kttdevelopment.mal4j.AccessToken
import com.kttdevelopment.mal4j.MyAnimeList
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator
import com.kttdevelopment.mal4j.anime.Anime
import com.kttdevelopment.mal4j.anime.AnimeListStatus
import com.kttdevelopment.mal4j.anime.property.AnimeStatus
import com.kttdevelopment.mal4j.anime.property.RewatchValue
import com.kttdevelopment.mal4j.manga.Manga
import com.kttdevelopment.mal4j.manga.property.MangaStatus
import com.kttdevelopment.mal4j.manga.property.RereadValue
import com.kttdevelopment.mal4j.property.Priority
import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.utils.SecretUtils.clientId
import src.main.kotlin.utils.SecretUtils.clientSecret

class MAL_Client(discordID: String) {

    private val access = DatabaseAccess().getAccess(discordID)

    private val mal: MyAnimeList = MyAnimeList.withAuthorization(MyAnimeListAuthenticator(
        clientId, clientSecret, access.code, access.verifier))

    fun getUserName() : String = mal.authenticatedUser.name

    fun postAnime(animeID: Long, episodes: Int, rewatching: Boolean = false,
                  timesRewatched: Int = 0, rewatchValue: RewatchValue = RewatchValue.High) {
        mal.updateAnimeListing(animeID)
            .status(AnimeStatus.Completed)
            .episodesWatched(episodes)
            .rewatching(rewatching)
            .priority(Priority.High)
            .timesRewatched(timesRewatched)
            .rewatchValue(rewatchValue)
            .update()
    }

    fun postManga(mangaID: Long, volumes: Int, chapters: Int) {
        mal.updateMangaListing(mangaID)
            .status(MangaStatus.Completed)
            .volumesRead(volumes)
            .chaptersRead(chapters)
            .rereading(false)
            .priority(Priority.High)
            .rereadValue(RereadValue.High)
            .update()
    }

    fun getAnime(id: Long): Anime = mal.getAnime(id)

    fun getManga(id: Long) : Manga = mal.getManga(id)

    fun refresh(): AccessToken = MyAnimeListAuthenticator(
        clientId, clientSecret, access.code, access.verifier
    ).refreshAccessToken()
}