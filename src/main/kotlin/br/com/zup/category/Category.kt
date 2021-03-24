package br.com.zup.category

import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Category(
    @field:NotBlank @Column(nullable = false, unique = true) val name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var createdAt: LocalDateTime = LocalDateTime.now()
}
