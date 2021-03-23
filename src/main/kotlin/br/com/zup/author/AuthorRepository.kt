package br.com.zup.author

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface AuthorRepository : JpaRepository<Author, Long> {
    fun findByEmail(email: String): Optional<Author>
}