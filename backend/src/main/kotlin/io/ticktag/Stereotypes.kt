package io.ticktag

import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@Repository
annotation class TicktagRepository

@RestController
annotation class TicktagRestInterface

// TODO: Add default security
@Service
@Transactional
annotation class TicktagService

@Service
annotation class TicktagLibrary
