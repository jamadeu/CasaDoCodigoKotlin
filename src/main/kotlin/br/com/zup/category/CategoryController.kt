package br.com.zup.category

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.validation.Valid

@Validated
@Controller("/categories")
class CategoryController(val categoryRepository: CategoryRepository) {

    @Post
    fun create(@Body @Valid request: NewCategoryRequest) {
        request.run {
            toCategory()
        }.also {
            categoryRepository.save(it)
        }
    }
}