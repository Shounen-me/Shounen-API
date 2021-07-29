package models

import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL


@Serializable
data class User(val id: String?, val userName: String) {

    private var profilePicture = "zero"
    private val animeList = mutableListOf<Anime>()
    fun addAnime(anime: String?) {
        anime?.let { Anime(it) }?.let { animeList.add(it) }
    }

    fun getAnime(): List<Anime> {
        return animeList
    }


    fun getProfilePicture(): String {
        return if (profilePicture == "zero") createWaifuPicture() else this.profilePicture
    }

    fun setProfilePicture(url: String): String {
        this.profilePicture = url
        return "Profile picture successfully set!"
        /*if (URL(url).toURI() != null) {
            this.profilePicture = url
            return "Profile picture succesfully set!"
        }
        return "Please enter a valid url for your profile picture."
         */
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

