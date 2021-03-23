package br.com.zup.author

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller("/authors")
class AuthorController(
    val authorRepository: AuthorRepository
) {

    @Post
    fun create(@Body @Valid request: NewAuthorRequest) {
        println("Request => $request")
        val author = request.toAuthor();
        authorRepository.save(author)
        println("Author => ${author.name}")
    }
}