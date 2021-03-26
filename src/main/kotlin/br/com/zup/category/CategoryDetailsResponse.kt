package br.com.zup.category

import com.fasterxml.jackson.annotation.JsonIgnore

data class CategoryDetailsResponse(
    @JsonIgnore val category: Category,
    val name: String = category.name
) {}