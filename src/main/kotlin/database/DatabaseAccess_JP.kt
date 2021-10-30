package src.main.kotlin.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import src.main.kotlin.models.japanese.GrammarEntry

class DatabaseAccess_JP {

    object grammar_database: Table() {
        val grammarPoint = varchar("grammarPoint", 255)
        val meaning = varchar("meaning", 255)
        val example = varchar("example", 255)
    }

    fun getGrammar(query: String): GrammarEntry {
        connect()
        var entry = GrammarEntry("", "", "")
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(DatabaseAccess.user_database)
                entry = grammar_database.select { grammar_database.grammarPoint eq query }
                    .first().toGrammarEntry()
            }
        } catch (e: NoSuchElementException) {
            println("An error occured. The database does not contain this specific grammar point yet.")
        }
        return entry
    }

    private fun ResultRow.toGrammarEntry() = GrammarEntry(
        query = this[grammar_database.grammarPoint],
        meaning = this[grammar_database.meaning],
        example_sentence = this[grammar_database.example]
    )

}