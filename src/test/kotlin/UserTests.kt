import src.main.kotlin.module
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import org.junit.Test
import src.main.kotlin.database.DatabaseAccess
import src.main.kotlin.functionality.generateVerifier
import src.main.kotlin.models.ProfilePicture
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.*


class UserTests {

    private val token: String = Files.readAllLines(Path.of("src/main/resources/secrets/authorized_keys.txt"))[0]
    private val db = DatabaseAccess()

    /*
    @Test
    fun getByNameAndID() {
        withTestApplication({ module(testing = true) }) {

            // Get own profile
            handleRequest(HttpMethod.Get, "/user/$token/166883258200621056").apply {
                assertEquals(
                    """{"id":"166883258200621056","userName":"Asuha","profilePicture":{"link":"https://i.waifu.pics/8TL6ycS.jpg"},"animeList":[""]}""",
                    response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }

            // With non authorized token
            handleRequest(HttpMethod.Get, "/user/123456/166883258200621056").apply {
                assertEquals("Unauthorized access.", response.content)
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }


            // Get by name
            handleRequest(HttpMethod.Get, "/user/$token/Asuha").apply {
                assertEquals(
                    """{"id":"166883258200621056","userName":"Asuha","profilePicture":{"link":"https://i.waifu.pics/8TL6ycS.jpg"},"animeList":[""]}""",
                    response.content
                )
            }

        }
    }


    @Test
    fun postUser() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/user/$token/166883258200621056/Asuha").apply{
               assertEquals("User stored correctly.", response.content)
                assertEquals(HttpStatusCode.Created, response.status())
            }

        }
    }


    @Test
    fun postAnime() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/user/$token/166883258200621056/anime/JoJo").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                // assertTrue(db.getUser("166883258200621056").animeList.contains("JoJo"))
            }
        }
    }

     */

    @Test
    fun getMAL() {
        withTestApplication({ module(testing = true) }) {
            val verifier = generateVerifier(128)

            handleRequest(HttpMethod.Get, "/mal/166883258200621056/sync/init").apply{
                println(response.content)
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Get, "/mal/redirect/$verifier/191").apply{
                println(response.content)
            }

            handleRequest(HttpMethod.Get, "/mal/sync/standard?code=def50200fb9aaea44f48f7ecf281a0385d152550fd9ab026e707f038f158dd8034d58a0e7c595a8e060fe5049b3e6f1bd0d131f4b8d027c244829b0747007b95941bf9c1072e944e797596031d88fe04c6c5a7f8fdd6d2c243d165810298d0366630fc9f6f3b9a7232ea3032976f6fc3697dcdf8c2c96b9103381a6f44a90ce803f66d3ebd37a7183a6cca806f8f59fa6218b1d073b27c4fb43b8008ebd54a16408a7814f290c0062e7ad335f817100a2a3f895e886bf1b5db177304bcc9ab518244e6f8346e16e63f1755b77eadc6ee78554fe2ea3c46b07e185aae6344e6a1c4edc5f05b40d62f89befcd37c7e508300bfa80900a291464b644d40330c4bd1eda104eeb4c6b87aa71b5fa0ebb2d1c4b95a45b2f2dda3c09ce74c533a19607d8251820927bfc3a77cb69ffea5be5ec48edb788326d69fe605a36c922130c562c86401cf29f0622a21d7a9afbefc7b3783cfd808e7bb10fe6ef1880804027af42c6c9a694efa8c3ca5767d9ce1cd54437df9a20bdcc9cbe0e6ca23bdceecfabb185f54dc0f23a06ed24cb2ceb2f8258e1173c86550f7e8d9d143090505e5b983bf9e4ef63679173dad51758cb14cf2fc8db4db964fe958f78c62e40d11730602a419f9de0f409b63953db44188f311ebce2429f3dad26e3e484e7df47f109663758eb5923d2817f664&state=RequestID191").apply{
                println(response.content)
                assertEquals(HttpStatusCode.OK, response.status())
            }

        }
    }


    @Test
    fun deleteUser() {

        // Delete existing user
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/user/$token/137874349846822912").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/user/123456/137453944237457408").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

        // Delete non existing user
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/$token/user/634011684843028509").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

}