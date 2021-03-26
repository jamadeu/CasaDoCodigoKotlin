package br.com.zup.book

import com.fasterxml.jackson.annotation.JsonIgnore

data class ListBookResponse(
    @JsonIgnore val book: Book,
    val bookId: Long? = book.id,
    val bookTitle: String = book.title
)
