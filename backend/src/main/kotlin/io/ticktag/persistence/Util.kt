package io.ticktag.persistence

import org.springframework.data.domain.Pageable

fun String.escapeHqlLike(escapeChar: Char): String {
    return this.replace("$escapeChar", "$escapeChar$escapeChar").replace("%", "$escapeChar%").replace("_", "${escapeChar}_")
}

fun Pageable.orderByClause(): String {
    return if (this.sort.none())
        " "
    else
        " order by ${this.sort.joinToString { "${it.property} ${it.direction}" }} "
}
