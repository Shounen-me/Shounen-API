package src.main.kotlin.database.postgres

import com.kttdevelopment.mal4j.MyAnimeList
import com.kttdevelopment.mal4j.MyAnimeListAuthenticator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import src.main.kotlin.models.anime.Access
import src.main.kotlin.models.anime.ProfilePicture
import src.main.kotlin.models.anime.User
import src.main.kotlin.utils.SecretUtils.clientId
import src.main.kotlin.utils.SecretUtils.clientSecret

// Database Quick Fix (relation xxx existiert nicht oder spalte xxx hat null values:
// Neuer Name für DB bzw neuen Table
// SchemaUtils.create(UserDatabase)
// SchemaUtils.createMissingTablesAndColumns(UserDatabase)

// TODO: Move SchemaUtils.create invocations to Singleton

class DatabaseAccess {
    object UserDatabase : Table() {
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
        var user = User("", "", ProfilePicture(""))

        try {
            transaction {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(UserDatabase)
                SchemaUtils.createMissingTablesAndColumns(UserDatabase)
                user =
                    if (wildcard.toLongOrNull() != null) {
                        UserDatabase.selectAll().where { UserDatabase.discordID eq wildcard }.first().toUser()
                    } else {
                        UserDatabase.selectAll().where { UserDatabase.userName eq wildcard }.first().toUser()
                    }
                user.setProfilePicture(user.getProfilePicture())
            }
        } catch (e: NoSuchElementException) {
            exposedLogger.error("User not found. Discord ID / Username: $wildcard")
        }

        return user
    }

    fun postUser(user: User) {
        transaction {
            addLogger(StdOutSqlLogger)
            UserDatabase.insert {
                it[discordID] = user.id
                it[userName] = user.userName
                it[profilePicture] = "zero"
                it[animeList] = 0
                it[malUsername] = "${user.userName} has not synced their MAL account yet."
                it[accessToken] = ""
                it[refreshToken] = ""
                it[verifier] = ""
                it[code] = ""
            } get UserDatabase.discordID
        }
    }

    fun postProfilePicture(
        id: String,
        link: String,
    ) {
        transaction {
            addLogger(StdOutSqlLogger)
            UserDatabase.update({ UserDatabase.discordID eq id }) {
                it[profilePicture] = link
            }
        }
    }

    fun deleteUser(discordID: String) {
        transaction { UserDatabase.deleteWhere { UserDatabase.discordID eq discordID } }
    }

    fun setVerifier(
        id: String,
        code: String,
    ) {
        transaction {
            UserDatabase.update({ UserDatabase.discordID eq id }) {
                it[verifier] = code
            }
        }
    }

    fun getAccess(id: String): Access {
        var access = Access("lol", "lol")
        transaction {
            access = UserDatabase.selectAll().where { UserDatabase.discordID eq id }.first().toAccess()
        }
        return access
    }

    fun setCode(
        id: String,
        s: String,
    ) {
        transaction {
            UserDatabase.update({ UserDatabase.discordID eq id }) {
                it[code] = s
            }
        }
    }

    private fun refresh(id: String) =
        MyAnimeList
            .withAuthorization(MyAnimeListAuthenticator(clientId, clientSecret, getAccess(id).code, getAccess(id).verifier))
            .refreshOAuthToken()

    private fun ResultRow.toUser() =
        User(
            id = this[UserDatabase.discordID],
            userName = this[UserDatabase.userName],
            profilePicture = ProfilePicture(this[UserDatabase.profilePicture]),
            animeList = this[UserDatabase.animeList],
            malUserName = this[UserDatabase.malUsername],
        )

    private fun ResultRow.toAccess() =
        Access(
            verifier = this[UserDatabase.verifier],
            code = this[UserDatabase.code],
        )
}
