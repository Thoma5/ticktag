package io.ticktag.util

import java.time.Duration
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext


class PositiveDurationValidator : ConstraintValidator<PositiveDuration, Duration> {
    override fun initialize(duration: PositiveDuration) {

    }

    override fun isValid(value: Duration?, context: ConstraintValidatorContext): Boolean {
        if (value == null)
            return true

        val isValid = !value.isNegative

        return isValid
    }
}