package io.ticktag.service.user.services

import io.ticktag.persistence.user.entity.Role
import io.ticktag.service.Principal
import io.ticktag.service.user.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface UserService {
    fun checkPassword(mail: String, password: String): UserResult?
    fun createUser(createUser: CreateUser, principal: Principal): UserResult
    fun getUser(id: UUID, principal: Principal): UserResult
    fun listUsers(query: String, role: Role?, principal: Principal, pageable: Pageable): Page<UserResult>
    fun listUsersInProject(projectId: UUID, principal: Principal): List<UserResult>
    fun listProjectUsers(projectId: UUID, principal: Principal): List<ProjectUserResult>
    fun listRoles(): List<RoleResult>
    fun updateUser(principal: Principal, id: UUID, updateUser: UpdateUser): UserResult
    fun getUserImage(imageId: TempImageId): ByteArray
    fun listUsersFuzzy(projectId: UUID, query: String, pageable: Pageable, principal: Principal): List<UserResult>
    fun getUsers(ids: Collection<UUID>, principal: Principal): Map<UUID, UserResult>
    fun getUserByUsername(username: String, principal: Principal): UserResult
}
