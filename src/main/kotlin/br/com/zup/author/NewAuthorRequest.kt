package br.com.zup.author

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class NewAuthorRequest(
    @field:NotBlank val name: String? = null,
    @field:NotBlank @field:Email val email: String? = null,
    @field:NotBlank @field:Size(max = 400) val description: String? = null
) {
    fun toAuthor(): Author {
        println("To model")
        return Author(name!!, email!!, description!!)
    }
}
