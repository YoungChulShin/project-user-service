package com.project.myservice.domain.user.authentication

import kotlin.random.Random

object UserAuthenticationNumberGenerator {

    /**
     * 0000 ~ 9999 까지의 임의의 번호 생성
     */
    fun generate() = Random.nextInt(from = 0, until = 9999).toString().padStart(4, '0')
}