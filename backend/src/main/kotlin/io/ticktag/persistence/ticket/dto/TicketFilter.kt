package io.ticktag.persistence.ticket.dto

import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.persistence.ticket.entity.Progress
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketTag
import io.ticktag.persistence.user.entity.User
import org.springframework.data.jpa.domain.Specification
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*
import javax.persistence.criteria.*


data class TicketFilter(val project: UUID, val numbers: List<Int>?, val title: String?, val tags: List<String>?,
                        val users: List<String?>?, val progressOne: Float?, val progressTwo: Float?,
                        val progressGreater: Boolean?, val dueDateOne: Instant?, val dueDateTwo: Instant?,
                        val dueDateGreater: Boolean?, val storyPointsOne: Int?, val storyPointsTwo: Int?,
                        val storyPointsGreater: Boolean?, val open: Boolean?, val parent: Int?) : Specification<Ticket> {


    val predicates = emptyList<Predicate>().toMutableList()

    override fun toPredicate(root: Root<Ticket>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {

        predicates.add(cb.equal(root.get<Project>("project").get<UUID>("id"), project))

        if (numbers != null) {
            predicates.add(cb.isTrue(root.get<Int>("number").`in`(numbers)))
        }
        if (title != null) {
            predicates.add(cb.like(cb.lower(root.get<String>("title")), title.toLowerCase().split(' ').joinToString("%", "%", "%")))
        }
        if (tags != null) {
            val join = root.join<Ticket, TicketTag>("tags")
            val ticketTagPath = root.get<TicketTag>("tags")
            query.multiselect(ticketTagPath)
            query.groupBy(root.get<UUID>("id"), root.get<Progress>("progress").get<Float>("totalProgress"))
            query.having(cb.greaterThanOrEqualTo(cb.count(join.get<TicketTag>("normalizedName")), tags.size.toLong()))
            val ttags = join.get<TicketTag>("normalizedName")
            predicates.add(cb.isTrue(ttags.`in`(tags)))
        }
        if (users != null) {
            val join = root.join<Ticket, AssignedTicketUser>("assignedTicketUsers", JoinType.LEFT)
            val userPath = root.get<User>("assignedTicketUsers")
            query.multiselect(userPath)
            query.groupBy(root.get<UUID>("id"), join.get<Ticket>("ticket").get<UUID>("id"), root.get<Progress>("progress").get<Float>("totalProgress"))
            val tusers = join.get<User>("user").get<String>("username")
            if(!users.contains("none")){
                predicates.add(cb.isTrue(tusers.`in`(users)))
            }
            else {
                predicates.add(cb.isNull(join.get<User>("user")))
            }
        }
        if (progressOne != null) {
            if (progressTwo != null) {
                predicates.add(cb.between(root.get<Progress>("progress").get<Float>("totalProgress"), progressOne, progressTwo))
            } else if (progressGreater != null) {
                if (progressGreater == true) {
                    predicates.add(cb.greaterThan(root.get<Progress>("progress").get<Float>("totalProgress"), progressOne))
                } else {
                    predicates.add(cb.lessThan(root.get<Progress>("progress").get<Float>("totalProgress"), progressOne))
                }
            } else {
                predicates.add(cb.equal(root.get<Progress>("progress").get<List<String>>("totalProgress"), progressOne))
            }
        }

        // We assume that the client sent us a valid UTC timestamp for the given day
        // We trim the clients timestamp down to the day, just to be sure
        // And filter everything between trimmed(now) <= date < trimmed(now + 1)
        if (dueDateOne != null) {
            if (dueDateTwo != null) {
                // We check for first <= date < second i.e. we can use exactly the method above
                predicates.add(cb.between(root.get<Instant>("dueDate"), normalizedCurrentDay(dueDateOne), normalizedNextDay(dueDateTwo)))
            } else if (dueDateGreater != null) {
                if (dueDateGreater == true) {
                    // first > date
                    predicates.add(cb.greaterThan(root.get<Instant>("dueDate"), normalizedCurrentDay(dueDateOne)))
                } else if (dueDateGreater == false) {
                    // first < date
                    predicates.add(cb.lessThan(root.get<Instant>("dueDate"), normalizedNextDay(dueDateOne)))
                }
            } else {
                // Instead of an exact match, we match between the two limits
                predicates.add(cb.between(root.get<Instant>("dueDate"), normalizedCurrentDay(dueDateOne), normalizedNextDay(dueDateOne)))
            }
        }
        if (storyPointsOne != null) {
            if (storyPointsTwo != null) {
                predicates.add(cb.between(root.get<Int>("storyPoints"), storyPointsOne, storyPointsTwo))
            } else if (storyPointsGreater != null) {
                if (storyPointsGreater == true) {
                    predicates.add(cb.greaterThan(root.get<Int>("storyPoints"), storyPointsOne))
                } else {
                    predicates.add(cb.lessThan(root.get<Int>("storyPoints"), storyPointsOne))
                }
            } else {
                predicates.add(cb.equal(root.get<Int>("storyPoints"), storyPointsOne))
            }
        }
        if (open != null) {
            predicates.add(cb.equal(root.get<Boolean>("open"), open))
        }
        if (parent != null) {
            val isChildOf: Predicate
            if (parent < 0) {
                isChildOf = cb.isNull(root.get<Ticket>("parentTicket"))
            } else {
                isChildOf = cb.equal(root.get<Ticket>("parentTicket").get<Int>("number"), parent)
            }
            predicates.add(isChildOf)
        }
        return if (predicates.size <= 0) {
            null
        } else {
            cb.and(*predicates.toTypedArray())
        }

    }

    private fun trimToDay(timestamp: Instant): Instant {
        return timestamp.truncatedTo(ChronoUnit.DAYS)
    }

    private fun normalizedCurrentDay(timestamp: Instant): Instant {
        return trimToDay(timestamp)
    }

    private fun normalizedNextDay(timestamp: Instant): Instant {
        return normalizedCurrentDay(timestamp).plus(1, ChronoUnit.DAYS)
    }
}