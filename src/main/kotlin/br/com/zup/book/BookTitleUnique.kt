package br.com.zup.book

import io.micronaut.context.annotation.Factory
import io.micronaut.validation.validator.constraints.ConstraintValidator
import javax.inject.Singleton
import javax.validation.Constraint

@MustBeDocumented
@Target(AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
annotation class BookTitleUnique(
    val message: String = "Book already in use"
)

@Factory
class BookTitleUniqueValidatorFactory(val bookRepository: BookRepository) {
    @Singleton
    fun BookTitleUniqueValidator(): ConstraintValidator<BookTitleUnique, String> {
        return ConstraintValidator { value, _, _ ->
            if (value == null) {
                true
            } else {
                !bookRepository.existsByTitle(value)
            }

        }
    }
}
