package com.project.myservice.domain.user

interface RoleRepository {

    fun save(role: Role): Role

    fun find(type: RoleType): Role?

    fun findByIds(idList: List<Long>): List<Role>
}