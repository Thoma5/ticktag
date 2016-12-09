package io.ticktag.restinterface.ticket.schema

import org.springframework.data.domain.Sort


enum class TicketSort(val order: Sort.Order) {
    NUMBER_ASC(Sort.Order(Sort.Direction.ASC, "number")),
    NUMBER_DESC(Sort.Order(Sort.Direction.DESC, "number")),
    TITLE_ASC(Sort.Order(Sort.Direction.ASC, "title")),
    TITLE_DESC(Sort.Order(Sort.Direction.DESC, "title")),
    DUE_DATE_ASC(Sort.Order(Sort.Direction.ASC, "dueDate")),
    DUE_DATE_DESC(Sort.Order(Sort.Direction.DESC, "dueDate")),
    STORY_POINTS_ASC(Sort.Order(Sort.Direction.ASC, "storyPoints")),
    STORY_POINTS_DESC(Sort.Order(Sort.Direction.DESC, "storyPoints")),
    PROGRESS_ASC(Sort.Order(Sort.Direction.ASC, "progress")),
    PROGRESS_DESC(Sort.Order(Sort.Direction.DESC, "progress"));
}

