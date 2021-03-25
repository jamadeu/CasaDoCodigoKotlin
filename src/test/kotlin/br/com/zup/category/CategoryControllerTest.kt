package br.com.zup.category

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
import javax.inject.Inject

@MicronautTest
class CategoryControllerTest(private val categoryRepository: CategoryRepository) {

    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient

    @BeforeEach
    fun setup() {
        categoryRepository.deleteAll()
    }

    @Test
    fun `Return 200 when create was category `() {
        val newCategoryRequest = NewCategoryRequest("Name")
        client.toBlocking()
            .exchange<NewCategoryRequest, Void>(HttpRequest.POST("/categories", newCategoryRequest))
            .also {
                assertEquals(HttpStatus.OK, it.status)
            }
        newCategoryRequest.name?.let { categoryRepository.findByName(it).orElseThrow() }
            .also {
                assertEquals(it!!.name, newCategoryRequest.name)
            }
    }

    @Test
    fun `Return 400 when category already exists`() {
        val newCategoryRequest = NewCategoryRequest("Name")
        client.toBlocking().also {
            it.exchange<NewCategoryRequest, Void>(HttpRequest.POST("/categories", newCategoryRequest))
        }.run {
            assertThrows<HttpClientResponseException> {
                exchange<NewCategoryRequest, Void>(HttpRequest.POST("/categories", newCategoryRequest))
            }
        }.also {
            assertAll(
                Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                Executable { assertTrue(it.message!!.contains("name: Category already in use", true)) }
            )
        }

        categoryRepository.findAll().also {
            assertEquals(1, it.size)
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `Return 400 when name is null or empty`(name: String?) {
        client.toBlocking().run {
            assertThrows<HttpClientResponseException> {
                exchange<NewCategoryRequest, Void>(HttpRequest.POST("/categories", NewCategoryRequest(name)))
            }
        }.also {
            assertAll(
                Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                Executable { assertTrue(it.message!!.contains("name: must not be blank", true)) }
            )
        }

        categoryRepository.findAll().also {
            assertEquals(0, it.size)
        }
    }

}