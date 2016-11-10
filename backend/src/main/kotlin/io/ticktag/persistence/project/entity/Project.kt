package io.ticktag.persistence.project.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "project")
open class Project {
    companion object {
        fun create(name: String, description: String, creationDate: Date, icon: ByteArray?): Project {
            val p = Project()
            p.id = UUID.randomUUID()
            p.name = name
            p.description = description
            p.creationDate = creationDate
            p.icon = icon
            return p
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "name", nullable = false)
    lateinit open var name: String

    @Column(name = "description", nullable = false)
    lateinit open var description: String

    @Column(name = "creation_date", nullable = false)
    lateinit open var creationDate: Date

    @Column(name = "icon", nullable = true)
    open var icon: ByteArray? = null

    protected constructor()
}