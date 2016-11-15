package io.ticktag.service.user.services

import io.ticktag.service.Principal
import io.ticktag.service.user.dto.CreateUser
import io.ticktag.service.user.dto.RoleResult
import io.ticktag.service.user.dto.UpdateUser
import io.ticktag.service.user.dto.UserResult
import java.util.*

interface UserService {
    fun checkPassword(mail: String, password: String): UserResult?
    fun createUser(createUser: CreateUser): UserResult
    fun getUser(id: UUID): UserResult?
    fun getUser(mail: String): UserResult?
    fun listUsers(): List<UserResult>
    fun listRoles(): List<RoleResult>
    fun updateUser(principal: Principal, id: UUID, updateUser: UpdateUser): UserResult
}
