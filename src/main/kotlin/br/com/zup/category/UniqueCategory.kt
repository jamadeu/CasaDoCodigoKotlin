package br.com.zup.category

import br.com.zup.author.AuthorRepository
import br.com.zup.author.UniqueEmail
import io.micronaut.context.annotation.Factory
import io.micronaut.validation.validator.constraints.ConstraintValidator
import javax.inject.Singleton
import javax.validation.Constraint

@MustBeDocumented
@Target(AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
annotation class UniqueCategory(
    val message: String = "Category already in use"
)

@Factory
class UniqueCategoryValidatorFactory(val categoryRepository: CategoryRepository) {
    @Singleton
    fun UniqueCategoryValidator(): ConstraintValidator<UniqueCategory, String> {
        return ConstraintValidator { value, _, _ ->
            if (value == null) {
                true
            } else {
                !categoryRepository.existsByName(value)
            }

        }
    }
}