package br.com.zup.author

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import java.lang.NullPointerException
import java.time.LocalDateTime
import javax.inject.Inject

@MicronautTest
internal class AuthorControllerTest(private val authorRepository: AuthorRepository) {

    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient


    @Test
    fun `Return status code 200 when author is created`() {
        val newAuthorRequest = NewAuthorRequest("Author", "author@test.com", "description")
        client.toBlocking().exchange<NewAuthorRequest, AuthorResponse>(HttpRequest.POST("/authors", newAuthorRequest))
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(it.status, HttpStatus.OK) }
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
        val newAuthorRequest = NewAuthorRequest("Author", email, "description")
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<NewAuthorRequest, AuthorResponse>(HttpRequest.POST("/authors", newAuthorRequest))
            }
        }.also {
            assertAll(
                Executable { assertEquals(it.status, HttpStatus.BAD_REQUEST) },
                Executable { assertTrue(it.localizedMessage.contains("request.email:")) }
            )
        }
    }



}



