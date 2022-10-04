package com.project.myservice.domain.user

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

class LocalRoleRepository : RoleRepository {

    val data = mutableListOf<Role>()
    var lastId = AtomicLong(1)

    override fun save(role: Role): Role {
        if (role.id == null) {
            val userClass = Role::class.java
            val idField = userClass.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(role, lastId.getAndIncrement())

            val createdAtField = userClass.superclass.getDeclaredField("createdAt")
            createdAtField.isAccessible = true
            createdAtField.set(role, Instant.now())
        }
        data.add(role)
        return role
    }

    override fun find(type: RoleType): Role? {
        val findRoles = data.filter { it.name == type.name }.toList()
        return if (findRoles.isNotEmpty()) findRoles[0] else null
    }
}