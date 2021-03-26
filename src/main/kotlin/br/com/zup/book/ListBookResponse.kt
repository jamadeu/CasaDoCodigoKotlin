package br.com.zup.book

import com.fasterxml.jackson.annotation.JsonIgnore


data class ListBookResponse(
    @JsonIgnore val book: Book,
    val bookId: Long? = book.id,
    val bookTitle: String = book.title
)
//fun listBookResponse(book: Book): Map<String, Any> {
//    return mapOf(
//        Pair("bookId", book.id!!),
//        Pair("bookTitle", book.title)
//    )
//}
