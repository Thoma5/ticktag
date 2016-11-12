package io.ticktag.persistence.member.entity

enum class ProjectRole {
    OBSERVER {
        override fun includesRole(projectRole: ProjectRole): Boolean {
            return projectRole == OBSERVER
        }
    },
    USER {
        override fun includesRole(projectRole: ProjectRole): Boolean {
            return projectRole == OBSERVER || projectRole == USER
        }
    },
    ADMIN {
        override fun includesRole(projectRole: ProjectRole): Boolean {
            return projectRole == OBSERVER || projectRole == USER || projectRole == ADMIN
        }
    };

    abstract fun includesRole(projectRole: ProjectRole): Boolean
}
