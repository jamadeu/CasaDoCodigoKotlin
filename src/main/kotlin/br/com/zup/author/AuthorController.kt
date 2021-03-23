package br.com.zup.author

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller("/authors")
class AuthorController(
    val authorRepository: AuthorRepository
) {

    @Get
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
    fun create(@Body @Valid request: NewAuthorRequest) {
        println("Request => $request")
        val author = request.toAuthor();
        authorRepository.save(author)
        println("Author => ${author.name}")
    }
}