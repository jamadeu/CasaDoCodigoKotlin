package br.com.zup.book

import br.com.zup.author.AuthorRepository
import br.com.zup.category.CategoryRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller("/books")
class BookController(
    val bookRepository: BookRepository,
    val categoryRepository: CategoryRepository,
    val authorRepository: AuthorRepository
) {
    @Get
    fun listAll(): HttpResponse<List<ListBookResponse>> {
        return bookRepository.findAll()
            .map {
                ListBookResponse(it)
            }.let {
                HttpResponse.ok(it)
            }
    }

    @Get("/{id}")
    fun findById(@PathVariable("id") idBook: Long): HttpResponse<Any> {
        bookRepository
            .run {
                findById(idBook)
            }.let {
                return if (it.isEmpty) {
                    HttpResponse.notFound("Book not found")
                } else {
                    HttpResponse.ok(BookDetailsResponse(it.get()))
                }
            }
    }

    @Post
    fun create(@Body @Valid request: NewBookRequest) {
        request
            .run { toBook(categoryRepository, authorRepository) }
            .also { bookRepository.save(it) }
    }
}