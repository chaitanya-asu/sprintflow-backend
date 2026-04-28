package com.sprintflow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CohortValidator.class)
@Documented
public @interface ValidCohort {
    String message() default "Cohort name must follow pattern 'C1', 'C2', or include a technology prefix like 'Java C1'.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
