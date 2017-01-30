package io.ticktag.service.user.services

import io.ticktag.persistence.user.entity.Role
import io.ticktag.service.Principal
import io.ticktag.service.user.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface UserService {
    /**
     * check if the supplied password is correct for this user
     * @param mail the email address is used to identify the user
     * @param password the plaintext password
     */
    fun checkPassword(mail: String, password: String): UserResult?

    /**
     * Creates a user
     */
    fun createUser(createUser: CreateUser, principal: Principal): UserResult

    /**
     * gets a specific user if it exits
     */
    fun getUser(id: UUID, principal: Principal): UserResult

    /**
     * List all users of a project. You need admin permissions for that
     */
    fun listUsers(query: String, role: Role?, disabled: Boolean?, principal: Principal, pageable: Pageable): Page<UserResult>

    /**
     * List all users of a projects. Therefore a user has to be a member of the project
     */
    fun listProjectUsers(projectId: UUID, disabled: Boolean?, principal: Principal): List<ProjectUserResult>

    /**
     * List all Roles a user can have.
     */
    fun listRoles(): List<RoleResult>

    /**
     * Update user with given set of properties in UpdateUser class
     */
    fun updateUser(principal: Principal, id: UUID, updateUser: UpdateUser): UserResult

    /**
     * get the profile picture of a user
     */
    fun getUserImage(imageId: TempImageId): ByteArray

    /**
     * List a small amount of user, which fit to the query
     * This function will be used for autocompletion
     */
    fun listUsersFuzzy(projectId: UUID, query: String, pageable: Pageable, principal: Principal): List<UserResult>

    /**
     * Get all users with the supplied ids.
     */
    fun getUsers(ids: Collection<UUID>, principal: Principal): Map<UUID, UserResult>

    /**
     * Find a user, which has the given username.
     */
    fun getUserByUsername(username: String, principal: Principal): UserResult

    /**
     * Delete a user permanently
     */
    fun deleteUser(id: UUID, principal: Principal)
}
