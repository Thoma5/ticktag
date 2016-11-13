package io.ticktag.service.user.services;

import io.ticktag.BaseTest
import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import io.ticktag.service.Principal
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken

import java.util.*
import javax.activation.DataSource
import javax.inject.Inject


class UserServiceTest : BaseTest() {
    @Inject lateinit var userService: UserService

    @Test
    fun test_checkPassword_negative(){
        val user = User.createWithId(UUID.fromString("00000000-0001-0000-0000-000000000001"), "", "", "", Role.ADMIN, UUID.fromString("00000000-0001-0000-0000-000000000001"))
        val p = Principal(UUID.fromString("00000000-0001-0000-0000-000000000001"), setOf("USER","OBSERVER","ADMIN"), user)
        SecurityContextHolder.getContext().authentication = PreAuthenticatedAuthenticationToken(p,null,p.authorities.map { SimpleGrantedAuthority(it) })
        val validLogin = this.userService.checkPassword("a@a.a", "fail")
        assertNull(validLogin)
    }

    @Test
    fun test_checkPassword_positive(){
        val user = User.createWithId(UUID.fromString("00000000-0001-0000-0000-000000000001"), "", "", "", Role.ADMIN, UUID.fromString("00000000-0001-0000-0000-000000000001"))
        val p = Principal(UUID.fromString("00000000-0001-0000-0000-000000000001"), setOf("USER","OBSERVER","ADMIN"), user)
        SecurityContextHolder.getContext().authentication = PreAuthenticatedAuthenticationToken(p,null,p.authorities.map { SimpleGrantedAuthority(it) })
        val validLogin = this.userService.checkPassword("a@a.a", "aaaa")
        assertNotNull(validLogin)
    }
}
