package br.com.zup.category

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class NewCategoryRequest(
    @field:NotBlank @field:UniqueCategory val name: String?
) {
    fun toCategory(): Category {
        return Category(name!!)
    }
}
