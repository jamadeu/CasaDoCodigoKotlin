package br.com.zup.category

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import javax.inject.Inject

@MicronautTest
class CategoryControllerTest(private val categoryRepository: CategoryRepository) {

    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient

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
            assertEquals(HttpStatus.BAD_REQUEST, it.status)
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
            assertEquals(HttpStatus.BAD_REQUEST, it.status)
        }

        categoryRepository.findAll().also {
            assertEquals(0, it.size)
        }
    }

}