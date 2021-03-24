package br.com.zup.book

import br.com.zup.author.Author
import br.com.zup.category.Category
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class Book (
    @field:NotBlank val title: String,
    @field:NotBlank val resume: String,
    @field:NotBlank val summary: String,
    @field:NotNull val value: BigDecimal,
    @field:NotNull val numberPages: Int,
    @field:NotBlank val isbn: String,
    @field:NotNull val publicationDate: LocalDate,
    @field:NotNull val category: Category,
    @field:NotNull val author: Author
){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var createdAt: LocalDateTime = LocalDateTime.now()

}
