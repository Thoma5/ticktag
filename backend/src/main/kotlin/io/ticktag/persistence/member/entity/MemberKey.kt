package io.ticktag.persistence.member.entity

import java.util.*
import java.io.Serializable;
import javax.persistence.*

@Embeddable
open class MemberKey : Serializable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "u_id", referencedColumnName = "id")
    lateinit open var uID: UUID
        protected set

    @ManyToOne(optional = false)
    @JoinColumn(name = "p_id", referencedColumnName = "id")
    lateinit open var pID: UUID
        protected set

}