package com.project.myservice.application.user.eventlistener

import com.project.myservice.domain.user.authentication.UserAuthenticationManager
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import com.project.myservice.domain.user.event.UserPasswordResetEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserPasswordResetEventListener(
    private val userAuthenticationManager: UserAuthenticationManager,
) {

    @Async
    @TransactionalEventListener
    fun handleUserPasswordResetEvent(event: UserPasswordResetEvent) {
        userAuthenticationManager.clearAuthentication(
            UserAuthenticationType.RESET_PASSWORD,
            event.userInfo.phoneNumber
        )
    }
}