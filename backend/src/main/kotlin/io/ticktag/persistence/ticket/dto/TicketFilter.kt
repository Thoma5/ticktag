package io.ticktag.persistence.ticket.dto

import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.persistence.ticket.entity.Progress
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketTag
import io.ticktag.persistence.user.entity.User
import org.springframework.data.jpa.domain.Specification
import java.time.Instant
import java.util.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root


data class TicketFilter(val project: UUID, val number: Int?, val title: String?, val tags: List<String>?,
                        val users: List<String>?, val progressOne: Float?, val progressTwo: Float?,
                        val progressGreater: Boolean?, val dueDateFrom: Instant?, val dueDateTwo: Instant?,
                        val dueDateGreater: Boolean?, val open: Boolean?) : Specification<Ticket> {


    val predicates = emptyList<Predicate>().toMutableList()

    override fun toPredicate(root: Root<Ticket>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {

         predicates.add(cb.equal(root.get<Project>("project").get<UUID>("id"), project))

        if (number != null) {
            predicates.add(cb.equal(root.get<Int>("number"), number))
        }
        if (title != null) {
            predicates.add(cb.like(cb.lower(root.get<String>("title")), "%"+title.toLowerCase()+"%"))
        }
        if (tags != null){
            val join = root.join<Ticket, TicketTag>("tags")
            val ticketTagPath = root.get<TicketTag>("tags")
            query.multiselect(ticketTagPath)
            query.groupBy(root.get<UUID>("id"))
            val ttags = join.get<TicketTag>("normalizedName")
            predicates.add(cb.isTrue(ttags.`in`(tags)))
        }
        if (users != null){
            val join = root.join<Ticket, AssignedTicketUser>("assignedTicketUsers")
            val userPath = root.get<User>("assignedTicketUsers")
            query.multiselect(userPath)
            query.groupBy(root.get<UUID>("id"),join.get<Ticket>("ticket").get<UUID>("id"))
            val tusers = join.get<User>("user").get<String>("username")
            predicates.add(cb.isTrue(tusers.`in`(users)))
        }
        if (progressOne != null) {
            if (progressTwo != null) {
                predicates.add(cb.between(root.get<Progress>("progress").get<Float>("progress"), progressOne, progressTwo))
            } else if (progressGreater != null) {
                if (progressGreater == true) {
                    predicates.add(cb.greaterThan(root.get<Progress>("progress").get<Float>("progress"), progressOne))
                } else {
                    predicates.add(cb.lessThanOrEqualTo(root.get<Progress>("progress").get<Float>("progress"), progressOne))
                }
            } else {
                predicates.add(cb.equal(root.get<Progress>("progress").get<List<String>>("progress"), progressOne))
            }
        }
        if (dueDateFrom != null && dueDateTwo != null) {
            predicates.add(cb.between(root.get<Instant>("dueDate"), dueDateFrom, dueDateTwo))
        } else if (dueDateFrom != null && dueDateTwo == null && dueDateGreater != null) {
            if (dueDateGreater == true) {
                predicates.add(cb.greaterThan(root.get<Instant>("dueDate"), dueDateFrom))
            } else {
                predicates.add(cb.lessThanOrEqualTo(root.get<Instant>("dueDate"), dueDateFrom))
            }
        }
        if (open != null) {
            predicates.add(cb.equal(root.get<Boolean>("open"), open))
        }
        return if (predicates.size <= 0) {
            null
        } else {
            cb.and(*predicates.toTypedArray())
        }

    }

}