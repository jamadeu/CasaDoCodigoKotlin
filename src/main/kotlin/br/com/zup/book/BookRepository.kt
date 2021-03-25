package br.com.zup.book

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface BookRepository : JpaRepository<Book, Long> {
    fun existsByTitle(title: String): Boolean
    fun findByTitle(title: String): Optional<Book>
}