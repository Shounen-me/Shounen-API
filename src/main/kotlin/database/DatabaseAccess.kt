package src.main.kotlin.database

import models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import src.main.kotlin.models.ProfilePicture
import java.nio.file.Files
import java.nio.file.Path

class DatabaseAccess {

    object users: Table() {
        val discordID = varchar("discordID", 255)
        val userName = varchar("userName", 255)
        val profilePicture = varchar("profilePicture", 255)
        val animeList = text("animeList")
    }


    fun getUser(wildcard: String): User { // wildcard = unique DiscordID or username
        connect()
        var user = User("", "", ProfilePicture(""), mutableListOf())
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
                it[animeList] = ""
            } get users.discordID
        }
    }

    fun postAnime(id: String, anime: String) {
        connect()
        transaction {
            addLogger(StdOutSqlLogger)
            val newText = users.select{ users.discordID eq id}.map{ it[users.animeList]}.first() + "$anime, "
            users.update({users.discordID eq id}) {
                it[animeList] = newText
            }
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


    private fun ResultRow.toUser() = User(
        id = this[users.discordID],
        userName = this[users.userName],
        profilePicture = ProfilePicture(this[users.profilePicture]),
        animeList = convertList(this[users.animeList])
    )

    private fun convertList(list: String): List<String> {
        return listOf(*list.split(",").toTypedArray())
    }


}