package io.ticktag.persistence.user.entity

enum class Role {
    USER {
        override fun includesRole(role: Role): Boolean {
            return role == USER
        }
    },
    OBSERVER {
        override fun includesRole(role: Role): Boolean {
            return role == USER || role == OBSERVER
        }
    },
    ADMIN {
        override fun includesRole(role: Role): Boolean {
            return role == USER || role == OBSERVER || role == ADMIN
        }
    };

    abstract fun includesRole(role: Role): Boolean
}
