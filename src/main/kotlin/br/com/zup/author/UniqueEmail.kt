package br.com.zup.author

import io.micronaut.context.annotation.Factory
import io.micronaut.validation.validator.constraints.ConstraintValidator
import javax.inject.Singleton
import javax.validation.Constraint
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FIELD

@MustBeDocumented
@Target(FIELD, CONSTRUCTOR)
@Retention(RUNTIME)
@Constraint(validatedBy = [])
annotation class UniqueEmail(
    val message: String = "Email already in use"
)

@Factory
class UniqueEmailValidatorFactory(val authorRepository: AuthorRepository) {
    @Singleton
    fun UniqueEmailValidator(): ConstraintValidator<UniqueEmail, String> {
        return ConstraintValidator { value, _, _ ->
            if (value == null) {
                true
            } else {
                !authorRepository.existsByEmail(value)
            }

        }
    }
}

