package br.com.zup.author

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
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


    @Test
    fun `Return status code 200 when author is created`() {
        val newAuthorRequest = NewAuthorRequest("Author", "author@test.com", "description")
        val response = client.toBlocking().exchange<Any, Any>(HttpRequest.POST("/authors", newAuthorRequest))
        val author = authorRepository.findByEmail(newAuthorRequest.email).orElseThrow()


        assertAll(
            Executable { assertNotNull(response) },
            Executable { assertEquals(response.status, HttpStatus.OK) },
            Executable { assertEquals(author.name, newAuthorRequest.name) },
            Executable { assertEquals(author.email, newAuthorRequest.email) },
            Executable { assertEquals(author.description, newAuthorRequest.description) },
            Executable { assertNotNull(author.createdAt) },
            Executable { assertTrue(author.createdAt.isBefore(LocalDateTime.now())) },
            Executable { assertNotNull(author.id) }
        )
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["invalidEmail"])
    fun `Return status 400 when email is null, empty or invalid`(email: String) {
        val newAuthorRequest = NewAuthorRequest("Author", email, "description")
        val response = client.toBlocking().exchange<Any, Any>(HttpRequest.POST("/authors", newAuthorRequest))

        assertAll(
            Executable { assertNotNull(response) },
            Executable { assertEquals(response.status, HttpStatus.BAD_REQUEST) }
        )
    }


}



