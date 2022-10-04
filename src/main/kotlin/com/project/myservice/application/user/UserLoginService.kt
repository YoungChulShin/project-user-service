package com.project.myservice.application.user

import com.project.myservice.common.exception.InvalidParameterException
import com.project.myservice.common.exception.UserNotFoundException
import com.project.myservice.config.security.LoginType
import com.project.myservice.domain.user.RoleRepository
import com.project.myservice.domain.user.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserLoginService(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
) : UserDetailsService {

    /**
     * login query param 정보를 바탕으로 사용자 정보 조회
     * - 입력 값 양식: {loginType}:{loginId}
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        val usernameItems = username?.split(":")
            ?: throw InvalidParameterException("로그인 정보 생성 중 오류가 발생했습니다")
        if (usernameItems.size != 2) {
            throw InvalidParameterException("로그인 정보 생성 중 오류가 발생했습니다")
        }

        val loginType = checkLoginType(usernameItems[0])
        val loginId = checkLoginId(usernameItems[1])
        val user = when(loginType) {
            LoginType.USERNAME -> userRepository.findByUsername(loginId)
            LoginType.EMAIL -> userRepository.findByEmail(loginId)
            LoginType.PHONE_NUMBER -> userRepository.findByPhoneNumber(loginId)
        } ?: throw UserNotFoundException()
        val userRoles = roleRepository.findByIds(user.roleIds)
            .map { SimpleGrantedAuthority(it.name) }
            .toList()

        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            userRoles
        )
    }

    private fun checkLoginType(loginTypeString: String): LoginType {
        try {
            return LoginType.valueOf(loginTypeString)
        } catch (e: Exception) {
            throw InvalidParameterException("'loginType' 파라미터 처리 중 에러가 발생했습니다")
        }
    }

    private fun checkLoginId(loginId: String): String {
        if (loginId.isEmpty()) {
            throw InvalidParameterException("'username' 파라미터 처리 중 에러가 발생했습니다")
        }

        return loginId
    }
}