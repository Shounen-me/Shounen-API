package models

import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request


@Serializable
data class User(val id: String?, val userName: String) {

    private var profilePicture = ""
    private val animeList = mutableListOf<Anime>()
    var averageRating: Double = animeList.stream().mapToDouble(){it.rating}.average().asDouble
    fun addAnime(anime: String?) {
        anime?.let { Anime(it) }?.let { animeList.add(it) }
    }

    fun getAnime(): List<Anime> {
        return animeList
    }

    // Used for user profile display in Discord
    fun getProfilePicture(): String {
        return if (profilePicture == "") createWaifuPicture() else this.profilePicture
    }

    fun setProfilePicture(picture: String) {
        this.profilePicture = picture
    }

    // Get random picture from Waifu.pics
    private fun createWaifuPicture(): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.waifu.pics/sfw/waifu")
            .get()
            .build()

        val picture = client.newCall(request).execute()
        return picture.body().string().subSequence(8, 40).toString()
    }


}


val userStorage = mutableListOf<User>(
    User("166883258200621056", "Asuha"),
    User("137453944237457408", "Iplayfair")
)

