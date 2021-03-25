package br.com.zup.book

fun listBookResponse(book: Book): Map<String, Any> {
    return mapOf(
        Pair("bookId", book.id!!),
        Pair("bookTitle", book.title)
    )
}
