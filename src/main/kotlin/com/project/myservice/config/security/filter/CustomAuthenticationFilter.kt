package com.project.myservice.config.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.myservice.config.security.token.TokenManager
import com.project.myservice.presentation.common.CommonResponse
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthenticationFilter(
    private val customAuthenticationManager: AuthenticationManager,
) : UsernamePasswordAuthenticationFilter() {

    /**
     * 아이디(로그인 아이디 or 핸드폰 번호 or 이메일)와 비밀번호를 이용한 인증 시도
     */
    override fun attemptAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ): Authentication {
        val loginType = request?.getParameter("loginType") ?: ""
        val loginId = request?.getParameter("loginId") ?: ""
        val password = request?.getParameter("password") ?: ""
        val authenticationToken =
            UsernamePasswordAuthenticationToken("${loginType}:${loginId}", password)

        return customAuthenticationManager.authenticate(authenticationToken)
    }

    /**
     * 인증이 성공하면 토큰 발급
     */
    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val user: User = authResult?.principal as User
        val tokenInfo = TokenManager.issueToken(
            sub = user.username,
            iss = request?.requestURI.toString(),
            roles = user.authorities.map { it.authority }.toList()
        )

        response?.let {
            it.contentType = MediaType.APPLICATION_JSON_VALUE
            ObjectMapper().writeValue(it.outputStream, CommonResponse.success(tokenInfo))
        }
    }
}