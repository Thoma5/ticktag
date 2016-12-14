package io.ticktag.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;


public class CheckDurationValidator implements ConstraintValidator<CheckDuration, Duration> {


    public void initialize(CheckDuration duration) {

    }

    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        boolean isValid;
        isValid = !value.isNegative();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{io.ticktag.checkduration.duration_may_not_be_negative}").addConstraintViolation();
        }
        return isValid;
    }


}