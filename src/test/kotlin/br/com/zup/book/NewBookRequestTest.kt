package br.com.zup.book

import br.com.zup.author.Author
import br.com.zup.author.AuthorRepository
import br.com.zup.category.Category
import br.com.zup.category.CategoryRepository
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@MicronautTest
internal class NewBookRequestTest {

    private val categoryRepository: CategoryRepository = mock(CategoryRepository::class.java)
    private val authorRepository: AuthorRepository = mock(AuthorRepository::class.java)
    private val category = Category(
        name = "Category"
    )
    private val author = Author(
        name = "Author",
        email = "author@test.com",
        description = "description"
    )

    private val newBookRequest = NewBookRequest(
        "Title",
        "resume",
        "summary",
        BigDecimal(20.00).setScale(2),
        100,
        "isbn",
        LocalDate.of(2030, 12, 12),
        1L,
        1L
    )

    @Test
    fun `Return a book when successful`() {
        `when`(categoryRepository.findById(newBookRequest.idCategory!!))
            .thenReturn(Optional.of(category))
        `when`(authorRepository.findById(newBookRequest.idAuthor!!))
            .thenReturn(Optional.of(author))

        val book = newBookRequest.toBook(categoryRepository, authorRepository)

        assertAll(
            Executable { assertNotNull(book) },
            Executable { assertEquals(book.title, newBookRequest.title) },
            Executable { assertEquals(book.resume, newBookRequest.resume) },
            Executable { assertEquals(book.summary, newBookRequest.summary) },
            Executable { assertEquals(book.value, newBookRequest.value) },
            Executable { assertEquals(book.numberPages, newBookRequest.numberPages) },
            Executable { assertEquals(book.isbn, newBookRequest.isbn) },
            Executable { assertEquals(book.publicationDate, newBookRequest.publicationDate) },
            Executable { assertEquals(book.category, category) },
            Executable { assertEquals(book.author, author) },
        )
    }

    @Test
    fun `Throws HttpClientResponseException when category was not found`() {
        `when`(categoryRepository.findById(newBookRequest.idCategory!!))
            .thenReturn(Optional.empty())
        `when`(authorRepository.findById(newBookRequest.idAuthor!!))
            .thenReturn(Optional.of(author))

        newBookRequest.run {
            assertThrows<HttpClientResponseException> {
                toBook(categoryRepository, authorRepository)
            }
        }.also {
            assertAll(
                Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                Executable { assertEquals("Category not found", it.message) }
            )
        }
    }

    @Test
    fun `Throws HttpClientResponseException when author was not found`() {
        `when`(categoryRepository.findById(newBookRequest.idCategory!!))
            .thenReturn(Optional.of(category))
        `when`(authorRepository.findById(newBookRequest.idAuthor!!))
            .thenReturn(Optional.empty())

        newBookRequest.run {
            assertThrows<HttpClientResponseException> {
                toBook(categoryRepository, authorRepository)
            }
        }.also {
            assertAll(
                Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                Executable { assertEquals("Author not found", it.message) }
            )
        }
    }


}