package com.project.myservice.config.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.time.Instant

object TokenManager {

    private const val JWT_SECRET = "secret"
    private val signAlgorithm = Algorithm.HMAC256(JWT_SECRET.encodeToByteArray())

    /**
     * JWT 토큰 생성
     * - accessToken: 유효시간 10분
     */
    fun issueToken(
        sub: String,
        iss: String,
        roles: List<String>
    ) = TokenInfo(
        JWT.create()
            .withSubject(sub)
            .withIssuer(iss)
            .withExpiresAt(Instant.now().plusMillis(10 * 60 * 1000))
            .withClaim("roles", roles)
            .sign(signAlgorithm)
    )

    /**
     * JWT 토큰 검증
     */
    fun verifyToken(token: String): DecodedJWT = JWT.require(signAlgorithm).build().verify(token)
}