package br.com.zup.book

import br.com.zup.author.AuthorRepository
import br.com.zup.category.CategoryRepository
import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.math.BigDecimal
import java.time.LocalDate
import javax.validation.constraints.*

@Introspected
data class NewBookRequest(
    @field:NotBlank @field:BookTitleUnique val title: String?,
    @field:NotBlank @field:Size(max = 500) val resume: String?,
    @field:NotBlank val summary: String?,
    @field:NotNull @field:Min(20) val value: BigDecimal?,
    @field:NotNull @field:Min(100) val numberPages: Int?,
    @field:NotBlank val isbn: String?,
    @field:NotNull @field:Future @JsonFormat(pattern = "yyyy-MM-dd") val publicationDate: LocalDate?,
    @field:NotNull val idCategory: Long?,
    @field:NotNull val idAuthor: Long?
) {

    fun toBook(
        categoryRepository: CategoryRepository,
        authorRepository: AuthorRepository
    ): Book {
        val category = idCategory?.let {
            categoryRepository.findById(it)
                .orElseThrow { HttpClientResponseException("Category not found", HttpResponse.badRequest<Void>()) }
        }
        val author = idAuthor?.let {
            authorRepository.findById(it)
                .orElseThrow { HttpClientResponseException("Author not found", HttpResponse.badRequest<Void>()) }
        }
        return Book(
            title!!,
            resume!!,
            summary!!,
            value!!,
            numberPages!!,
            isbn!!,
            publicationDate!!,
            category!!,
            author!!
        )
    }
}
