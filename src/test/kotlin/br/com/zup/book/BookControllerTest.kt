package br.com.zup.book

import br.com.zup.author.Author
import br.com.zup.author.AuthorRepository
import br.com.zup.category.Category
import br.com.zup.category.CategoryRepository
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
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@MicronautTest
internal class BookControllerTest(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val categoryRepository: CategoryRepository
) {

    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient


    private var category: Category? = null
    private var author: Author? = null

    @BeforeEach
    fun setup() {
        bookRepository.deleteAll()
        categoryRepository.deleteAll()
        authorRepository.deleteAll()
        category = categoryRepository.save(Category("Category"))
        author = authorRepository.save(Author("Author", "author@test.com", "description"))
    }

    @Test
    fun `Return 200 when book was created`() {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal(20.00).setScale(2),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking().exchange<NewBookRequest, Void>(
            HttpRequest.POST("/books", newBookRequest)
        )
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.OK, it.status) }
                )
            }

        newBookRequest.title?.let { bookRepository.findByTitle(it).orElseThrow() }
            .also {
                assertAll(
                    Executable { assertEquals(newBookRequest.title, it!!.title) },
                    Executable { assertEquals(newBookRequest.resume, it!!.resume) },
                    Executable { assertEquals(newBookRequest.summary, it!!.summary) },
                    Executable { assertEquals(newBookRequest.value, it!!.value) },
                    Executable { assertEquals(newBookRequest.numberPages, it!!.numberPages) },
                    Executable { assertEquals(newBookRequest.isbn, it!!.isbn) },
                    Executable { assertEquals(newBookRequest.publicationDate, it!!.publicationDate) },
                    Executable { assertEquals(newBookRequest.idCategory, it!!.category.id) },
                    Executable { assertEquals(newBookRequest.idAuthor, it!!.author.id) }
                )
            }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `Return 400 when title is null or empty`(title: String?) {
        val newBookRequest = NewBookRequest(
            title,
            "resume",
            "summary",
            BigDecimal(20.00).setScale(2),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("title: must not be blank", true)) }
                )
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `Return 400 when title already exists`() {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal(20.00).setScale(2),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .also {
                it.exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
            }
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("title: Book already in use", true)) }
                )
            }
        bookRepository.findAll()
            .also {
                assertEquals(1, it.size)
            }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(
        strings = [
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum et nunc ex. Donec convallis, nisl volutpat " +
                    "venenatis iaculis, purus eros condimentum nisl, sit amet volutpat metus eros sit amet nunc. Orci varius " +
                    "natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Morbi sollicitudin ligula " +
                    "iaculis est sollicitudin porta. Pellentesque vehicula lectus quis lacus tempor, quis suscipit leo " +
                    "vehicula. Aliquam vitae arcu tellus. Nam viverra quis metus et scelerisque sodales sed."
        ]
    )
    fun `Return 400 when resume is null, empty or has over than 500 characters`(resume: String?) {
        val newBookRequest = NewBookRequest(
            "Title",
            resume,
            "summary",
            BigDecimal(20.00).setScale(2),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("resume: ", true)) }
                )
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `Return 400 when summary is null or empty`(summary: String?) {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            summary,
            BigDecimal(20.00).setScale(2),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("summary: must not be blank")) }
                )
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `Return 400 when value is null`() {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            null,
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("value: must not be null", true)) }
                )
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @ParameterizedTest
    @ValueSource(doubles = [19.99, 5.0])
    fun `Return 400 when value less than 20`(value: Double) {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal.valueOf(value),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("value: must be greater than or equal to 20", true)) }
                )
            }

        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = [99, 20])
    fun `Return 400 when numberPages less than 100 or null`(numberPages: Int?) {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal(20.00).setScale(2),
            numberPages,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("numberPages", true)) }
                )
                println(it.message)
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `Return 400 when isbn is null or empty`(isbn: String?) {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal(20.00).setScale(2),
            100,
            isbn,
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("isbn: must ", true)) }
                )
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `Return 400 when publicationDate is null`() {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal(20.00).setScale(2),
            100,
            "isbn",
            null,
            category?.id,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("publicationDate: must not be null")) }
                )
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `Return 400 when publicationDate is not future`() {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal(20.00).setScale(2),
            100,
            "isbn",
            LocalDate.now(),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("publicationDate: must be a future date", true)) }
                )
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `Return 400 when idCategory is null`() {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal.valueOf(20.00),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            null,
            author?.id
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("idCategory: must not be null", true)) }
                )
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `Return 400 when idAuthor is null`() {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal.valueOf(20.00),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            null
        )
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
                }
            }
            .also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.BAD_REQUEST, it.status) },
                    Executable { assertTrue(it.message!!.contains("idAuthor: must not be null")) }
                )
            }
        bookRepository.findAll()
            .also { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `Return 200 and list of ListBookResponse`() {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal(20.00).setScale(2),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .also {
                it.exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
            }
            .run {
                exchange<Void, List<Map<String, Any>>>(
                    HttpRequest.GET("/books")
                )
            }.also {
                assertEquals(HttpStatus.OK, it.status)
            }.body().also {
                it?.map {
                    assertAll(
                        Executable { assertTrue(it.containsKey("bookId")) },
                        Executable { assertTrue(it.containsKey("bookTitle")) },
                        Executable { assertTrue(it.containsValue(newBookRequest.title!!)) }
                    )
                }
            }
    }

    @Test
    fun `Return book details`() {
        val newBookRequest = NewBookRequest(
            "Title",
            "resume",
            "summary",
            BigDecimal(20.00).setScale(2),
            100,
            "isbn",
            LocalDate.of(2030, 12, 12),
            category?.id,
            author?.id
        )
        client.toBlocking()
            .also {
                it.exchange<NewBookRequest, Void>(HttpRequest.POST("/books", newBookRequest))
            }.run {
                val id = bookRepository.findByTitle(newBookRequest.title!!).orElseThrow().id
                exchange<Void, List<Map<String, Any>>>(HttpRequest.GET("/books/$id"))
            }.also {
                assertAll(
                    Executable { assertNotNull(it) },
                    Executable { assertEquals(HttpStatus.OK, it.status) }
                )
            }.body()
            .also {
                it?.map {
                    assertAll(
                        Executable { assertEquals(newBookRequest.title, it["title"]) },
                        Executable { assertEquals(newBookRequest.resume, it["resume"]) },
                        Executable { assertEquals(newBookRequest.summary, it["summary"]) },
                        Executable { assertEquals(newBookRequest.publicationDate, it["publicationDate"]) },
                        Executable { assertEquals(newBookRequest.numberPages, it["numberPages"]) },
                        Executable { assertEquals(newBookRequest.isbn, it["isbn"]) },
                        Executable { assertEquals(newBookRequest.value, it["value"]) },
                        Executable {
                            assertEquals(
                                mapOf(
                                    Pair("name", author?.name),
                                    Pair("email", author?.email),
                                    Pair("description", author?.description)
                                ), it["author"]
                            )
                        },
                        Executable {
                            assertEquals(
                                mapOf(
                                    Pair("name", category?.name),
                                ), it["category"]
                            )
                        }
                    )
                }
            }
    }

    @Test
    fun `Return 404 when book was not found by id`() {
        client.toBlocking()
            .run {
                assertThrows<HttpClientResponseException> {
                    exchange<Void, String>(HttpRequest.GET("/books/1"))
                        .also {
                            assertAll(
                                Executable { assertNotNull(it) },
                                Executable { assertNotNull(it.body()) },
                                Executable { assertEquals("Book not found", it.body()) }
                            )
                        }
                }
            }
    }

}