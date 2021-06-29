package models

import kotlinx.serialization.Serializable

@Serializable
data class Anime(val id: String, val name: String, val rating: Double, val synopsis: String) {

    fun contains(s: String?): String? {
        when {
            s.equals("id") -> return id
            s.equals("name") -> return name
            s.equals("rating") -> return rating.toString()
            s.equals("synopsis") -> return synopsis
        }
        return null
    }

}

val animeStorage = listOf(
    Anime("1", "Naruto", 8.5, "Ya boi Naruto"),
    Anime("2", "Chainsaw Man", 10.0, "Woof Woof")
)
