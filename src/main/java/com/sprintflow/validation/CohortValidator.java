package com.sprintflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CohortValidator implements ConstraintValidator<ValidCohort, String> {

    private static final String VALID_COHORT_PATTERN = "^(?i)(.*?\\s*)?C\\d+$";

    @Override
    public void initialize(ValidCohort constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return value.matches(VALID_COHORT_PATTERN);
    }
}

