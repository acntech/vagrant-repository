package no.acntech.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CorrectVersionValidator.class)
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectVersion {

    String message() default "{acntech.validation.constraints.CorrectVersion.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
