package br.com.zup.book

import br.com.zup.author.AuthorDetailsResponse
import br.com.zup.category.CategoryDetailsResponse
import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigDecimal

data class BookDetailsResponse(
    @JsonIgnore val book: Book,
    val title: String = book.title,
    val resume: String = book.resume,
    val summary: String = book.summary,
    val value: BigDecimal = book.value,
    val numberPages: Int = book.numberPages,
    val isbn: String = book.isbn,
    val author: AuthorDetailsResponse = AuthorDetailsResponse(book.author),
    val category: CategoryDetailsResponse = CategoryDetailsResponse(book.category)
) {}
