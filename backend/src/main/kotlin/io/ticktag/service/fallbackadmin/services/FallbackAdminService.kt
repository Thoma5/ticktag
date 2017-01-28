package io.ticktag.service.fallbackadmin.services

interface FallbackAdminService {
    /**
     * Function will be used to ensure that there is always an admin
     */
    fun ensureAdminExists()
}