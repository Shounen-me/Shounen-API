package src.main.kotlin.models.anime

import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request


@Serializable
data class User(val id: String, val userName: String,
                val profilePicture: ProfilePicture = ProfilePicture("zero"),
                var animeList: Int = 0, var malUserName: String = "$userName has not synced their MAL account yet.") {

    fun getProfilePicture(): String {
        return if (profilePicture.link == "zero") createWaifuPicture() else this.profilePicture.link
    }

    fun setProfilePicture(url: String): String {
        this.profilePicture.link = url
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
        return picture.body()!!.string().subSequence(8, 40).toString()
    }




}




