package src.main.kotlin.database

import com.google.gson.Gson
import functionality.AnimeList
import functionality.client
import functionality.refreshToken
import kotlinx.serialization.Serializable
import models.User
import okhttp3.FormBody
import okhttp3.Request
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import src.main.kotlin.models.ProfilePicture

// Database Quick Fix (relation xxx existiert nicht oder spalte xxx hat null values:
// Neuer Name für DB bzw neuen Table
// SchemaUtils.create(UserDatabase)
// SchemaUtils.createMissingTablesAndColumns(UserDatabase)

class DatabaseAccess {

    object UserDatabase: Table() {
        val discordID = varchar("discordID", 255)
        val userName = varchar("userName", 255)
        val profilePicture = varchar("profilePicture", 255)
        val animeList = integer("animeList")
        val malUsername = varchar("malUsername", 255)
        val accessToken = varchar("accessToken", 255)
        val refreshToken = varchar("refreshToken", 255)
        val verifier = varchar("verifier", 255)
    }


    fun getUser(wildcard: String): User { // wildcard = unique DiscordID or username
        connect()
        var user = User("", "", ProfilePicture(""))
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(UserDatabase)
                SchemaUtils.createMissingTablesAndColumns(UserDatabase)
                user = if (wildcard.toLongOrNull() != null) {
                    UserDatabase.select { UserDatabase.discordID eq wildcard }.first().toUser()
                } else {
                    UserDatabase.select { UserDatabase.userName eq wildcard }.first().toUser()
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
            UserDatabase.insert {
                it[discordID] = user.id
                it[userName] = user.userName
                it[profilePicture] = "zero"
                it[animeList] = 0
                it[malUsername] = "/"
                it[accessToken] = ""
                it[refreshToken] = ""
                it[verifier] = ""
            } get UserDatabase.discordID
        }
    }


    fun postAnime(discordID: String, animeID: String): Boolean {
        connect()
        transaction {
            addLogger(StdOutSqlLogger)
            refresh(discordID)
            val tokens = transaction {
                UserDatabase.select { UserDatabase.discordID eq discordID }.first().toTokens()
            }
            val request = Request.Builder()
                .url("https://api.myanimelist.net/v2/anime/$animeID/my_list_status")
                .addHeader("Authorization", "Bearer ${tokens.access_token}")
                .put(FormBody.Builder().build()).build()
            val response = client.newCall(request).execute()
            return@transaction response.code() == 200
        }
        return false
    }

    fun postProfilePicture(id: String, link: String) {
        connect()
        transaction {
            addLogger(StdOutSqlLogger)
            UserDatabase.update({UserDatabase.discordID eq id}) {
                it[profilePicture] = link
            }
        }
    }

    fun deleteUser(discordID: String) {
        connect()
        transaction { UserDatabase.deleteWhere { UserDatabase.discordID eq discordID } }
    }

    // First time sync to MAL account
    fun syncMalToDB(discordID: String, access: String, refresh: String) {
        connect()
        transaction {
            addLogger(StdOutSqlLogger)
            UserDatabase.update({UserDatabase.discordID eq id}) {
                it[accessToken] = access
                it[refreshToken] = refresh
            }
            val userName = getMALUserName(discordID)
            println(userName)
            /*
            UserDatabase.update({UserDatabase.discordID eq id}) {
                it[malUsername] = userName
            }
             */
        }
    }


    // Get user name from MyAnimeList
    private fun getMALUserName(discordID: String): String {
        refresh(discordID)
        val tokens = transaction {
            UserDatabase.select { UserDatabase.discordID eq discordID }.first().toTokens()
        }
        val request = Request.Builder()
            .url("https://api.myanimelist.net/v2/users/@me?fields=name")
            .addHeader("Authorization", "Bearer ${tokens.access_token}")
            .get().build()
        val response = client.newCall(request).execute()
        return Gson().fromJson(response.body().string(), String::class.java)
    }


    // Fetch current (completed) anime list from MAL
    fun fetchCompletedAnime(discordID: String): Array<String> {
        refresh(discordID)
        val tokens = transaction {
            UserDatabase.select { UserDatabase.discordID eq discordID }.first().toTokens()
        }
        val request = Request.Builder()
            .url("https://api.myanimelist.net/v2/users/@me/animeList?status=completed&limit=1000")
            .addHeader("Authorization", "Bearer ${tokens.access_token}")
            .get().build()
        val response = client.newCall(request).execute()
        return Gson().fromJson(response.body().string(), AnimeList::class.java).data
    }


    // Fetch the ten latest anime on the user's 'currently watching' list
    fun fetchWatchingAnime(discordID: String): Array<String> {
        refresh(discordID)
        val tokens = transaction {
            UserDatabase.select { UserDatabase.discordID eq discordID }.first().toTokens()
        }
        val request = Request.Builder()
            .url("https://api.myanimelist.net/v2/users/@me/animeList?status=watching&limit=10")
            .addHeader("Authorization", "Bearer ${tokens.access_token}")
            .get().build()
        val response = client.newCall(request).execute()
        return Gson().fromJson(response.body().string(), AnimeList::class.java).data
    }


    private fun refresh(discordID: String) {
        val tokens = refreshToken(discordID)
        transaction {
            UserDatabase.update({UserDatabase.discordID eq id}) {
                it[accessToken] = tokens.access_token
                it[refreshToken] = tokens.refresh_token
            }
        }
    }

    fun setVerifier(id: String, code: String) {
        connect()
        transaction {
            UserDatabase.update({UserDatabase.discordID eq id}) {
                it[verifier] = code
            }
        }
    }

    fun getVerifier(id: String): String {
        connect()
        transaction {
            return@transaction UserDatabase.select { UserDatabase.discordID eq id }.first()[UserDatabase.verifier]
        }
        return "User not found."
    }


    private fun ResultRow.toUser() = User(
        id = this[UserDatabase.discordID],
        userName = this[UserDatabase.userName],
        profilePicture = ProfilePicture(this[UserDatabase.profilePicture]),
        animeList = this[UserDatabase.animeList],
        malUserName = this[UserDatabase.malUsername]
    )


    @Serializable
    data class Token (val access_token: String, val refresh_token: String)

    private fun ResultRow.toTokens() = Token(
        access_token = this[UserDatabase.accessToken],
        refresh_token = this[UserDatabase.refreshToken]
    )


}