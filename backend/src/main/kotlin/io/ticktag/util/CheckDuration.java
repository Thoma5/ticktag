package io.ticktag.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = CheckDurationValidator.class)
@Documented
public @interface CheckDuration {

    String message() default "{io.ticktag.checkduration" +
            "duration_may_not_be_negative}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    @Target({ElementType.FIELD})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        CheckDuration[] value();
    }
}