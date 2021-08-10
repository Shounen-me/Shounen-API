import src.main.kotlin.module
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import org.junit.Test
import src.main.kotlin.database.DatabaseAccess
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