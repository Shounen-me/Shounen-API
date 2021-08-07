package src.main.kotlin.database

import com.google.gson.Gson
import functionality.AnimeList
import functionality.client
import functionality.refreshToken
import kotlinx.serialization.Serializable
import models.User
import okhttp3.Request
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import src.main.kotlin.models.ProfilePicture

class DatabaseAccess {

    object users: Table() {
        val discordID = varchar("discordID", 255)
        val userName = varchar("userName", 255)
        val profilePicture = varchar("profilePicture", 255)
        val animeList = integer("animeList")
        val malUsername = varchar("malUsername", 255)
        val accessToken = varchar("accessToken", 255)
        val refreshToken = varchar("refreshToken", 255)
    }


    fun getUser(wildcard: String): User { // wildcard = unique DiscordID or username
        connect()
        var user = User("", "", ProfilePicture(""),0)
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                // SchemaUtils.create(users)
                user = if (wildcard.toLongOrNull() != null) {
                    users.select { users.discordID eq wildcard }.first().toUser()
                } else {
                    users.select { users.userName eq wildcard }.first().toUser()
                }
                user.setProfilePicture(user.getProfilePicture())
            }
        } catch (e: NoSuchElementException) { }
        return user
    }

    fun postUser(user: User) {
        connect()
        transaction {
            addLogger(StdOutSqlLogger)
            users.insert {
                it[discordID] = user.id
                it[userName] = user.userName
                it[profilePicture] = "zero"
                it[animeList] = 0
                it[malUsername] = "/"
                it[accessToken] = ""
                it[refreshToken] = ""
            } get users.discordID
        }
    }


    fun postAnime(id: String, anime: String) {
        connect()
        transaction {
            addLogger(StdOutSqlLogger)

        }
    }

    fun postProfilePicture(id: String, link: String) {
        connect()
        transaction {
            addLogger(StdOutSqlLogger)
            users.update({users.discordID eq id}) {
                it[profilePicture] = link
            }
        }
    }

    fun deleteUser(discordID: String) {
        connect()
        transaction { users.deleteWhere { users.discordID eq discordID } }
    }

    // First time sync to MAL account
    fun syncMalToDB(discordID: String, access: String, refresh: String) {
        connect()
        transaction {
            addLogger(StdOutSqlLogger)
            users.update({users.discordID eq id}) {
                it[accessToken] = access
                it[refreshToken] = refresh
            }
            val userName = getMALUserName(discordID)
            users.update({users.discordID eq id}) {
                it[malUsername] = userName
            }
        }
    }


    // Get user name from MyAnimeList
    private fun getMALUserName(discordID: String): String {
        // https://api.myanimelist.net/v2/users/@me?fields=anime_statistics
        refresh(discordID)
        val tokens = transaction {
            users.select { users.discordID eq discordID }.first().toTokens()
        }
        val request = Request.Builder()
            .url("https://api.myanimelist.net/v2/users/@me?fields=name")
            .addHeader("Authorization", "Bearer ${tokens.access_token}")
            .get().build()
        val response = client.newCall(request).execute()
        return Gson().fromJson(response.body().string(), String::class.java)
    }


    // Fetch current anime list from MAL
    fun fetch_list(discordID: String): AnimeList? {
        refresh(discordID)
        val tokens = transaction {
            users.select { users.discordID eq discordID }.first().toTokens()
        }
        val request = Request.Builder()
            .url("https://api.myanimelist.net/v2/users/@me/animeList?limit=1000")
            .addHeader("Authorization", "Bearer ${tokens.access_token}")
            .get().build()
        val response = client.newCall(request).execute()
        return Gson().fromJson(response.body().string(), AnimeList::class.java)
    }


    private fun refresh(discordID: String) {
        val tokens = refreshToken(discordID)
        transaction {
            users.update({users.discordID eq id}) {
                it[accessToken] = tokens.access_token
                it[refreshToken] = tokens.refresh_token
            }
        }
    }


    private fun ResultRow.toUser() = User(
        id = this[users.discordID],
        userName = this[users.userName],
        profilePicture = ProfilePicture(this[users.profilePicture]),
        animeList = this[users.animeList]
    )


    @Serializable
    data class Token (val access_token: String, val refresh_token: String)

    private fun ResultRow.toTokens() = Token(
        access_token = this[users.accessToken],
        refresh_token = this[users.refreshToken]
    )


}