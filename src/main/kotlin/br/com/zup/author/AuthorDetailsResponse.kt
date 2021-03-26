package br.com.zup.author

import com.fasterxml.jackson.annotation.JsonIgnore

data class AuthorDetailsResponse(
    @JsonIgnore val author: Author,
    val name: String = author.name,
    val email: String = author.email,
    val description: String = author.description
) {}