package src.main.kotlin.database

import com.kttdevelopment.mal4j.MyAnimeList
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator
import src.main.kotlin.models.anime.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import src.main.kotlin.models.anime.Access
import src.main.kotlin.models.anime.ProfilePicture
import src.main.kotlin.utils.SecretUtils.clientId
import src.main.kotlin.utils.SecretUtils.clientSecret

// Database Quick Fix (relation xxx existiert nicht oder spalte xxx hat null values:
// Neuer Name für DB bzw neuen Table
// SchemaUtils.create(UserDatabase)
// SchemaUtils.createMissingTablesAndColumns(UserDatabase)

class DatabaseAccess {

    object user_database: Table() {
        val discordID = varchar("discordID", 255)
        val userName = varchar("userName", 255)
        val profilePicture = varchar("profilePicture", 1000)
        val animeList = integer("animeList")
        val malUsername = varchar("malUsername", 255)
        val accessToken = varchar("accessToken", 255)
        val refreshToken = varchar("refreshToken", 255)
        val verifier = varchar("verifier", 255)
        val code = varchar("code", 1024)
    }



    fun getUser(wildcard: String): User { // wildcard = unique DiscordID or username
        connect()
        var user = User("", "", ProfilePicture(""))
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(user_database)
                SchemaUtils.createMissingTablesAndColumns(user_database)
                user = if (wildcard.toLongOrNull() != null) {
                    user_database.select { user_database.discordID eq wildcard }.first().toUser()
                } else {
                    user_database.select { user_database.userName eq wildcard }.first().toUser()
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
            user_database.insert {
                it[discordID] = user.id
                it[userName] = user.userName
                it[profilePicture] = "zero"
                it[animeList] = 0
                it[malUsername] = "${user.userName} has not synced their MAL account yet."
                it[accessToken] = ""
                it[refreshToken] = ""
                it[verifier] = ""
                it[code] = ""
            } get user_database.discordID
        }
    }


    fun postProfilePicture(id: String, link: String) {
        connect()
        transaction {
            addLogger(StdOutSqlLogger)
            user_database.update({user_database.discordID eq id}) {
                it[profilePicture] = link
            }
        }
    }

    fun deleteUser(discordID: String) {
        connect()
        transaction { user_database.deleteWhere { user_database.discordID eq discordID } }
    }

    fun setVerifier(id: String, code: String) {
        connect()
        transaction {
            user_database.update({user_database.discordID eq id}) {
                it[verifier] = code
            }
        }
    }

    fun getAccess(id: String) : Access {
        connect()
        var access = Access("lol", "lol")
        transaction {
            access = user_database.select { user_database.discordID eq id }.first().toAccess()
        }
        return access
    }

    fun setCode(id: String, s: String) {
        connect()
        transaction {
            user_database.update({user_database.discordID eq id}) {
                it[code] = s
            }
        }
    }

    private fun refresh(id: String) =
        MyAnimeList.withAuthorization(
            MyAnimeListAuthenticator(clientId, clientSecret, getAccess(id).code, getAccess(id).verifier)
        ).refreshOAuthToken()

    private fun ResultRow.toUser() = User(
        id = this[user_database.discordID],
        userName = this[user_database.userName],
        profilePicture = ProfilePicture(this[user_database.profilePicture]),
        animeList = this[user_database.animeList],
        malUserName = this[user_database.malUsername]
    )

    private fun ResultRow.toAccess() = Access(
        verifier = this[user_database.verifier],
        code = this[user_database.code]
    )


}