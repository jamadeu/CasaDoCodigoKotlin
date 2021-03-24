package br.com.zup.book

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface BookRepository : JpaRepository<Book, Long> {
    fun existsByTitle(title: String): Boolean
}