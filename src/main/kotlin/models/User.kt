package models

import kotlinx.serialization.Serializable


@Serializable
data class User(val id: String, val userName: String, val email: String) {
    private val animeList = mutableListOf<Anime>()
    fun addAnime(anime: Anime) {
        animeList.add(anime)
    }
}


val userStorage = mutableListOf<User>(
    User("1", "Asuha", "manu@asuha.dev"),
    User("2", "Iplayfair", "iplayfair@asuha.dev")
)
