package src.main.kotlin.database.redis

import com.google.gson.Gson
import redis.clients.jedis.Jedis
import src.main.kotlin.models.japanese.GrammarEntry
import utils.JapaneseConverter

class GrammarDatabase {

    private val jedis = Jedis()

    // Store new GrammarEntry
    fun storeGrammar(entry: GrammarEntry) {
        encodeEntry(entry)
        val entryString = Gson().toJson(entry)
        jedis.set(entry.query, entryString)
    }

    // Get GrammarEntry
    fun getGrammar(query: String): GrammarEntry {
        return Gson().fromJson(jedis.get(query), GrammarEntry::class.java)
    }

    private fun encodeEntry(entry: GrammarEntry) {
        val converter = JapaneseConverter()
        entry.query = converter.encodeJapanese(entry.query)
        entry.example_sentence = converter.encodeJapanese(entry.example_sentence)
    }

}