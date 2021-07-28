package models

import kotlinx.serialization.Serializable

@Serializable
data class Anime(val name: String, val rating: Double = 0.0) {

    fun contains(s: String?): String? {
        when {
            s.equals("name") -> return name
            s.equals("rating") -> return rating.toString()
        }
        return null
    }

}

val animeStorage = listOf(
    Anime( "Naruto", 8.5),
    Anime("Chainsaw Man", 10.0)
)
