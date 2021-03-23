package br.com.zup.author

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
data class Author(
    @field:NotBlank var name: String,
    @field:NotBlank @field:Email var email: String,
    @field:NotBlank @field:Size(max = 400) var description: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var createdAt: LocalDateTime = LocalDateTime.now()
}
