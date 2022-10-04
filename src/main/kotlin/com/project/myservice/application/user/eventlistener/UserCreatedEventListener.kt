package com.project.myservice.application.user.eventlistener

import com.project.myservice.domain.user.authentication.UserAuthenticationManager
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import com.project.myservice.domain.user.event.UserCreatedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserCreatedEventListener(
    val authenticationManager: UserAuthenticationManager,
) {

    @TransactionalEventListener
    fun handleUserCreatedEvent(event: UserCreatedEvent) {
        authenticationManager.clearAuthentication(
            UserAuthenticationType.CREATE_USER,
            event.userInfo.phoneNumber
        )
    }
}