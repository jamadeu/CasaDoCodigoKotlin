package br.com.zup.author

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDateTime
import javax.inject.Inject

@MicronautTest
internal class AuthorControllerTest(private val authorRepository: AuthorRepository) {

    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient

    @BeforeEach
    fun setup() {
        authorRepository.deleteAll()
    }

    @Test
    fun `Return status code 200 when author was created`() {
        val newAuthorRequest = NewAuthorRequest("Author", "author@test.com", "description")
        client.toBlocking().exchange<NewAuthorRequest, Void>(
            HttpRequest.POST(
                "/authors", newAuthorRequest
            )
        )
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.OK, it.status) }
                )
            }

        newAuthorRequest.email?.let { authorRepository.findByEmail(it).orElseThrow() }
            .also {
                assertAll(
                    Executable { assertEquals(it!!.name, newAuthorRequest.name) },
                    Executable { assertEquals(it!!.email, newAuthorRequest.email) },
                    Executable { assertEquals(it!!.description, newAuthorRequest.description) },
                    Executable { assertNotNull(it!!.createdAt) },
                    Executable { assertTrue(it!!.createdAt.isBefore(LocalDateTime.now())) },
                    Executable { assertNotNull(it!!.id) }
                )
            }
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    @ValueSource(strings = ["invalidEmail"])
    fun `Return status 400 when email is null, empty or invalid`(email: String?) {
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<NewAuthorRequest, Void>(
                    HttpRequest.POST(
                        "/authors",
                        NewAuthorRequest("Author", email, "description")
                    )
                )
            }
        }.also {
            assertAll(
                Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                Executable { assertTrue(it.localizedMessage.contains("request.email:")) }
            )
        }

        authorRepository.findAll().also {
            assertTrue(it.isEmpty())
        }
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    fun `Return status 400 when name is empty or null`(name: String?) {
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<NewAuthorRequest, Void>(
                    HttpRequest.POST(
                        "/authors",
                        NewAuthorRequest(name, "email@test.com", "description")
                    )
                )
            }
        }.also {
            assertEquals(it.status, HttpStatus.BAD_REQUEST)
        }

        authorRepository.findAll().also {
            assertTrue(it.isEmpty())
        }
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    @ValueSource(
        strings = [
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque rhoncus enim ac convallis tincidunt. " +
                    "Vestibulum commodo tincidunt sagittis. Suspendisse sit amet faucibus velit, feugiat consectetur nisi. " +
                    "Fusce vel enim dui. Nam diam odio, blandit ac orci nec, vestibulum varius magna. Maecenas in hendrerit justo. " +
                    "Nulla dolor ligula, pulvinar id turpis quis, semper suscipit sapien. Vivamus quam."
        ]
    )
    fun `Return status 400 when description is empty, null or has over than 400 characters`(description: String?) {
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<NewAuthorRequest, Void>(
                    HttpRequest.POST(
                        "/authors",
                        NewAuthorRequest("Name", "email@test.com", description)
                    )
                )
            }
        }.also {
            assertEquals(HttpStatus.BAD_REQUEST, it.status)
        }

        authorRepository.findAll().also {
            assertTrue(it.isEmpty())
        }
    }


    @Test
    fun `Return status 400 when email already in use`() {
        val newAuthorRequest = NewAuthorRequest("Name", "email@test.com", "description")

        client.toBlocking().also {
            it.exchange<NewAuthorRequest, Void>(HttpRequest.POST("/authors", newAuthorRequest))
        }.run {
            assertThrows<HttpClientResponseException> {
                exchange<NewAuthorRequest, Void>(HttpRequest.POST("/authors", newAuthorRequest))
            }
        }.also {
            assertAll(
                Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                Executable { assertTrue(it.localizedMessage.contains("request.email:")) }
            )
        }

        authorRepository.findAll().also {
            assertEquals(1, it.size)
        }
    }
}



