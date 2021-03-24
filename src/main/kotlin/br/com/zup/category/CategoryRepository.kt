package br.com.zup.category

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByName(name: String): Optional<Category>
    fun existsByName(value: String): Boolean
}