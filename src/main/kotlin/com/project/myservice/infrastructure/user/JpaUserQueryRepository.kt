package com.project.myservice.infrastructure.user

import com.project.myservice.domain.user.QRole.role
import com.project.myservice.domain.user.QUser.user
import com.project.myservice.domain.user.UserDetailInfo
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class JpaUserQueryRepository(
    val queryFactory: JPAQueryFactory
) {

    fun findDetail(username: String): UserDetailInfo? {
        return queryFactory
            .from(user)
            .innerJoin(role).on(user.roleIds.contains(role.id)).fetchJoin()
            .where(user.username.eq(username))
            .transform(
                GroupBy.groupBy(user.id).list(
                    Projections.constructor(
                        UserDetailInfo::class.java,
                        user.id,
                        user.username,
                        user.email,
                        user.phoneNumber,
                        user.name,
                        user.nickname,
                        GroupBy.list(
                            Projections.constructor(
                                String::class.java,
                                role.name
                            )
                        ),
                        user.createdAt,
                        user.updatedAt,
                        user.deletedAt
                    )
                )
            ).let {
                if (it.size >= 1) it[0] else null
            }
    }
}