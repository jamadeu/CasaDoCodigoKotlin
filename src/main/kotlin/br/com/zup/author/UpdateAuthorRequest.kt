package br.com.zup.author

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Email
import javax.validation.constraints.Size

@Introspected
data class UpdateAuthorRequest(
    val name: String?,
    @field:Email val email: String?,
    @field:Size(max = 400) val description: String?
)
