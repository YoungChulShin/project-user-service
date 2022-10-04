package com.project.myservice.infrastructure.user

import com.project.myservice.domain.user.Role
import com.project.myservice.domain.user.RoleRepository
import com.project.myservice.domain.user.RoleType
import org.springframework.stereotype.Repository

@Repository
class DefaultRoleRepository(
    val jpaRoleRepository: JpaRoleRepository,
) : RoleRepository {

    override fun save(role: Role) = jpaRoleRepository.save(role)

    override fun find(type: RoleType) = jpaRoleRepository.findByName(type.name)
}