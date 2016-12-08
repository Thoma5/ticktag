package io.ticktag.service.ticket.dto

import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.ticket.entity.Progress
import io.ticktag.persistence.ticket.entity.Ticket
import org.springframework.data.jpa.domain.Specification
import java.util.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root


class TicketFilter : Specification<Ticket>{

    val predicates = emptyList<Predicate>().toMutableList()

    override fun toPredicate(root: Root<Ticket>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {
        if (project != null) {
            predicates.add(cb.equal(root.get<Project>("project").get<UUID>("id"), project))
        }
        if (number != null){
            predicates.add(cb.equal(root.get<Int>("number"),number))
        }
        if (title != null){
            predicates.add(cb.equal(root.get<String>("title"),title))
        }
      /* if (tags != null){
            predicates.add(cb.isTrue((root.get<List<TicketTag>>("tags").get<List<String>>("normalizedName")).`in`(tags)))
        }
        if (users != null){
            predicates.add(cb.isTrue((root.get<List<String>>("assignedTicketUsers")).`in`(users)))
        } TODO:fix*/
        if (progressFrom != null) {
            if (progressTo != null) {
                predicates.add(cb.between(root.get<Progress>("progress").get<Float>("progress"), progressFrom, progressTo))
            } else if (progressTo == null && progressGreater != null) {
                if (progressGreater == true) {
                    predicates.add(cb.greaterThan(root.get<Progress>("progress").get<Float>("progress"), progressFrom))
                } else {
                    predicates.add(cb.lessThanOrEqualTo(root.get<Progress>("progress").get<Float>("progress"), progressFrom))
                }
            } else if (progressTo == null && progressGreater == null) {
                predicates.add(cb.equal(root.get<Progress>("progress").get<List<String>>("progress"), progressFrom))
            }
        }
        if (dueFrom != null && dueTo != null){
            predicates.add(cb.between(root.get<Date>("dueDate"),dueFrom,dueTo))
        }
        else if (dueFrom != null && dueTo == null && dueAfter!=null){
            if(dueAfter == true){
                predicates.add(cb.greaterThan(root.get<Date>("dueDate"),dueFrom))
            }
            else {
                predicates.add(cb.lessThanOrEqualTo(root.get<Date>("dueDate"),dueFrom))
            }
        }
        if (open != null){
            predicates.add(cb.equal(root.get<Boolean>("open"),open))
        }
        return if (predicates.size <= 0) {
            null
        }
        else{
            cb.and(*predicates.toTypedArray())
        }

    }
    var project: UUID? = null
    var number: Int? = null
    var title: String? = null
    var tags: List<String>? = null
    var users: List<String>? = null
    var progressFrom: Float? = null
    var progressTo: Float? = null
    var progressGreater: Boolean? = null
    var dueFrom: Date? = null
    var dueTo: Date? = null
    var dueAfter: Boolean? = null
    var open: Boolean? = null

}