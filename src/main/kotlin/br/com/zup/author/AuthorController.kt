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
        request
            .run {
                toAuthor()
            }.also {
                authorRepository.save(it)
            }
    }
}