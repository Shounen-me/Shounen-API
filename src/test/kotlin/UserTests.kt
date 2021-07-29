import src.main.kotlin.module
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import models.Anime
import models.userStorage
import org.junit.Test
import src.main.kotlin.models.ProfilePicture
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class UserTests {


    @Test
    fun getByNameAndID() {
        withTestApplication({ module(testing = true) }) {

            // Get own profile
            handleRequest(HttpMethod.Get, "/user/166883258200621056").apply {
                assertEquals(
                    """{"id":"166883258200621056","userName":"Asuha","profilePicture":"zero","animeList":[]}""",
                    response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }


            // Get by name
            handleRequest(HttpMethod.Get, "/user/search/Iplayfair").apply {
                val content = response.content
                println("2. $content")
                assertEquals(
                    """{"id":"137453944237457408","userName":"Iplayfair","profilePicture":"zero","animeList":[]}""",
                    content
                )
                // assertEquals(HttpStatusCode.OK, response.status())
            }


            // Get by ID
            handleRequest(HttpMethod.Get, "/user/search/137453944237457408").apply {
                val content = response.content
                println("3. $content")
                assertEquals(
                    """{"id":"137453944237457408","userName":"Iplayfair","profilePicture":"zero","animeList":[]}""",
                    content
                )
                // assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }


    @Test
    fun postUser() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/user/137874349846822912/Weska").apply {
                assertNotNull(userStorage.find { it.userName == "Weska" })
                assertNotNull(userStorage.find { it.id == "137874349846822912" })
            }
        }
    }


    @Test
    fun postAnime() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/user/166883258200621056/anime/JoJo").apply {
                userStorage.find { it.id == "166883258200621056" }
                    ?.let { assertTrue(it.getAnime().contains(Anime("JoJo"))) }
            }
        }
    }


    @Test
    fun deleteUser() {

        // Delete existing user
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/user/166883258200621056").apply {
                assertNull(userStorage.find { it.id == "166883258200621056" })
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }

        // Delete non existing user
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/user/634011684843028509").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

}