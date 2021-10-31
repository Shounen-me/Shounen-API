import src.main.kotlin.database.redis.GrammarDatabase
import src.main.kotlin.models.japanese.GrammarEntry
import java.nio.charset.Charset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GrammarTest {

    @Test
    fun testStore() {
        val entry = GrammarEntry("たびに", "Whenever ...", "スーパーに行くたびに、鶏肉を買う。")
        val db = GrammarDatabase()
        println(entry)
        db.storeGrammar(entry)
        val storedEntry = db.getGrammar("たびに")
        assertNotNull(storedEntry)
        assertEquals(storedEntry, entry)
        println(storedEntry)
    }

}