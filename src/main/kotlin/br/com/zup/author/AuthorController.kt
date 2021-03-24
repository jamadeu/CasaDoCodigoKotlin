package br.com.zup.author

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Controller("/authors")
class AuthorController(
    val authorRepository: AuthorRepository
) {

    @Post
    @Transactional
    fun create(@Body @Valid request: NewAuthorRequest) {
        request
            .run {
                toAuthor()
            }.also {
                authorRepository.save(it)
            }
    }
}