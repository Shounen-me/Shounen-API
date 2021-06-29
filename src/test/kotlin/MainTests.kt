import src.main.kotlin.module
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import kotlin.test.assertEquals


class MainTests {
    @Test
    fun testGetAnime() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/anime/1").apply {
                assertEquals(
                    """{"id":"1","name":"Naruto","rating":8.5,"synopsis":"Ya boi Naruto"}""",
                    response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

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

}