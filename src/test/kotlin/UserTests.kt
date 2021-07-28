import src.main.kotlin.module
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import models.userStorage
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class UserTests {

    @Test
    fun testGetUser() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/user/1").apply {
                assertEquals(
                    """{"id":"1","userName":"Asuha","email":"manu@asuha.dev","animeList":[]}""",
                    response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    // 137874349846822912
    @Test
    fun postUser() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/user/137874349846822912/Weska").apply {
                assertNotNull(userStorage.find { it.userName == "Weska" })
            }
        }
    }

}