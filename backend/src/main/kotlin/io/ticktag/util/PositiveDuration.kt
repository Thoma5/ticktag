package io.ticktag.util

import javax.validation.Constraint
import javax.validation.Payload

import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = arrayOf(PositiveDurationValidator::class))
@MustBeDocumented
annotation class PositiveDuration(
        val message: String = "{io.ticktag.positiveduration" + "duration_not_negative}",
        val groups: Array<KClass<*>> = arrayOf(),
        val payload: Array<KClass<out Payload>> = arrayOf())