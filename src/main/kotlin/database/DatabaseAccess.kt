package src.main.kotlin.database

import com.kttdevelopment.mal4j.MyAnimeList
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator
import kotlinx.serialization.Serializable
import models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import src.main.kotlin.models.ProfilePicture
import sync.*

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
        val code = varchar("code", 255)
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
                user.malUserName = getMALUserName(wildcard)
                println(user.malUserName)
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
                it[code] = ""
            } get UserDatabase.discordID
        }
    }


    fun postAnime(discordID: String, animeID: String): Boolean {
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



    // Get user name from MyAnimeList
    private fun getMALUserName(discordID: String): String {
        refresh(discordID)
        return MyAnimeList
            .withAuthorization(MyAnimeListAuthenticator(clientId, clientSecret, getCode(discordID), getVerifier(discordID)))
            .authenticatedUser.name
    }
    /*


    // Fetch current (completed) anime list from MAL

    fun fetchCompletedAnime(discordID: String): Array<String> {
        refresh(discordID)

    }



    // Fetch the ten latest anime on the user's 'currently watching' list
    fun fetchWatchingAnime(discordID: String): Array<String> {
        refresh(discordID)

    }

     */


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

    private fun getCode(id: String): String {
        connect()
        transaction {
            return@transaction UserDatabase.select { UserDatabase.discordID eq id }.first()[UserDatabase.code]
        }
        return "User not found."
    }

    fun setCode(id: String, s: String) {
        connect()
        transaction {
            UserDatabase.update({UserDatabase.discordID eq id}) {
                it[code] = s
            }
        }
    }

    private fun refresh(id: String) = MyAnimeList.withAuthorization(MyAnimeListAuthenticator(clientId, clientSecret, getCode(id), getVerifier(id))).refreshOAuthToken()



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