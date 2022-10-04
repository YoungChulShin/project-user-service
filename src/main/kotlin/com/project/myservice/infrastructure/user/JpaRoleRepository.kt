package com.project.myservice.infrastructure.user

import com.project.myservice.domain.user.Role
import org.springframework.data.jpa.repository.JpaRepository

interface JpaRoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: String): Role?

    fun findByIdIn(idList: List<Long>): List<Role>
}