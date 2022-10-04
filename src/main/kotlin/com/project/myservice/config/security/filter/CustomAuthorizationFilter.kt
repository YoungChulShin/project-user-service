package com.project.myservice.config.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.myservice.common.exception.ErrorCode
import com.project.myservice.config.security.token.TokenManager
import com.project.myservice.presentation.common.CommonResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthorizationFilter : OncePerRequestFilter() {

    companion object {
        private const val URL_LOGIN = "/api/v1/login"
        private const val AUTHORIZATION_PREFIX = "bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 로그인이면 토큰 체크를 하지 않는다
        if (request.servletPath.equals(URL_LOGIN)) {
            filterChain.doFilter(request, response)
            return
        }

        // 로그인이 아니면 토큰을 검증한다
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorizationHeader?.startsWith(AUTHORIZATION_PREFIX) == true) {
            try {
                val token = authorizationHeader.substring(AUTHORIZATION_PREFIX.length)
                val decodedToken = TokenManager.verifyToken(token)

                val username = decodedToken.subject
                val roles = decodedToken.getClaim("roles").asArray(String::class.java)
                val authorities = mutableListOf<SimpleGrantedAuthority>()
                roles.forEach { authorities.add(SimpleGrantedAuthority(it)) }

                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(username, null, authorities)
                filterChain.doFilter(request, response)
            } catch (e: Exception) {
                response.status = HttpStatus.FORBIDDEN.value()
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                e.message?.let {
                    val errorResponse = CommonResponse.fail(
                        ErrorCode.COMMON_INVALID_TOKEN.name,
                        e.message ?: ErrorCode.COMMON_INVALID_TOKEN.errorMessage
                    )
                    ObjectMapper().writeValue(response.outputStream, errorResponse)
                }
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }
}