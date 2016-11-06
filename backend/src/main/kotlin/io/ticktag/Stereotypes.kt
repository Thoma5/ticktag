package io.ticktag

import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.ticktag.restinterface.ValidationErrorJson
import io.ticktag.service.AuthExpr
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Repository
annotation class TicktagRepository

@RestController
@RequestMapping(produces = arrayOf("application/json"), consumes = arrayOf("application/json"))
@ApiResponses(
    ApiResponse(code = 422, message = "Input Validation Error", response = ValidationErrorJson::class, responseContainer = "List")
)
annotation class TicktagRestInterface

@Service
@Transactional
@PreAuthorize(AuthExpr.NOBODY)
annotation class TicktagService

@Service
annotation class TicktagLibrary
