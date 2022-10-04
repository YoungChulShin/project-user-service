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
) : BaseEntity() {

    constructor(
        username: String,
        email: String,
        phoneNumber: String,
        password: String,
        name: String,
        nickname: String,
    ) : this(null, username, email, phoneNumber, password, name, nickname)

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null


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