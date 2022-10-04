package com.project.myservice.domain.user

import com.project.myservice.domain.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "roles")
class Role private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name")
    val name: String
) : BaseEntity() {

    companion object {
        fun create(type: RoleType) = Role(null, type.name)
    }
}