package br.com.zup.book

import br.com.zup.author.AuthorRepository
import br.com.zup.category.CategoryRepository
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller("/books")
class BookController (
    val bookRepository: BookRepository,
    val categoryRepository: CategoryRepository,
    val authorRepository: AuthorRepository
    ){

    @Post
    fun create(@Body @Valid request: NewBookRequest){
        request.run {
            toBook(categoryRepository, authorRepository)
        }.also {
            bookRepository.save(it)
        }
    }
}