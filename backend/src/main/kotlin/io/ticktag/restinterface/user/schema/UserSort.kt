package io.ticktag.restinterface.user.schema

import org.springframework.data.domain.Sort

enum class UserSort(val order: Sort.Order) {
    USERNAME_ASC(Sort.Order(Sort.Direction.ASC, "username")),
    USERNAME_DESC(Sort.Order(Sort.Direction.DESC, "username")),
    NAME_ASC(Sort.Order(Sort.Direction.ASC, "name")),
    NAME_DESC(Sort.Order(Sort.Direction.DESC, "name")),
    MAIL_ASC(Sort.Order(Sort.Direction.ASC, "mail")),
    MAIL_DESC(Sort.Order(Sort.Direction.DESC, "mail")),
    ROLE_ASC(Sort.Order(Sort.Direction.ASC, "role")),
    ROLE_DESC(Sort.Order(Sort.Direction.DESC, "role"));
}
