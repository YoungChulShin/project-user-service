package com.project.myservice.domain.user

import com.project.myservice.domain.BaseEntity
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "username")
    val username: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "phone_number")
    val phoneNumber: String,

    @Column(name = "password")
    var password: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "nickname")
    var nickname: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role_id")
    @CollectionTable(name = "user_roles")
    val roleIds: MutableList<Long> = mutableListOf(),
) : BaseEntity() {

    constructor(
        username: String,
        email: String,
        phoneNumber: String,
        password: String,
        name: String,
        nickname: String,
        roleId: Long,
    ) : this(null, username, email, phoneNumber, password, name, nickname, mutableListOf(roleId))

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null

    fun addRole(roleId: Long) {
        if (this.roleIds.contains(roleId)) return

        this.roleIds.add(roleId)
    }

    fun resetPassword(newPassword: String) {
        // 비밀번호 초기화는 이전 비밀번호와 비교하지 않는다
        this.password = newPassword
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (id != null) {
            return true
        }

        if (username != other.username) return false
        if (email != other.email) return false
        if (phoneNumber != other.phoneNumber) return false
        if (password != other.password) return false
        if (name != other.name) return false
        if (nickname != other.nickname) return false
        if (deletedAt != other.deletedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + (deletedAt?.hashCode() ?: 0)
        return result
    }
}