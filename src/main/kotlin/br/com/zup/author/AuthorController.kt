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

    @Get
    @Transactional
    fun list(@QueryValue(defaultValue = "") email: String): HttpResponse<Any> {
        if (email.isEmpty()) {
            val authorList = authorRepository.findAll();
            val response = authorList.map { author -> AuthorResponse(author) }
            return HttpResponse.ok(response)
        }

        val optional = authorRepository.findByEmail(email);
        if (optional.isEmpty) {
            return HttpResponse.notFound()
        }

        return HttpResponse.ok(AuthorResponse(optional.get()))
    }

    @Post
    @Transactional
    fun create(@Body @Valid request: NewAuthorRequest) {
        val author = request.toAuthor();
        authorRepository.save(author)
    }

    @Put("/{id}")
    @Transactional
    fun update(@PathVariable("id") id: Long, @Body @Valid request: UpdateAuthorRequest): HttpResponse<Any> {
        val optionalAuthor = authorRepository.findById(id)
        if (optionalAuthor.isEmpty) {
            return HttpResponse.notFound()
        }

        val author = optionalAuthor.get()
        when {
            request.name != null -> author.name = request.name
            request.email != null -> author.email = request.email
            request.description != null -> author.description = request.description
        }

        authorRepository.update(author)
        return HttpResponse.ok(AuthorResponse(author))
    }

    @Delete("/{id}")
    @Transactional
    fun delete(@PathVariable("id") id: Long): HttpResponse<Any> {
        if (authorRepository.findById(id).isEmpty) {
            return HttpResponse.notFound()
        }
        authorRepository.deleteById(id)
        return HttpResponse.noContent()
    }

}