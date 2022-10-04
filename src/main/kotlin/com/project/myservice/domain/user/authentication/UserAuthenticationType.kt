package com.project.myservice.domain.user.authentication

enum class UserAuthenticationType(
    /**
     * 사용자 정보 필수 여부
     */
    val userOption: UserExistsOption
) {

    /**
     * 회원가입 인증
     */
    CREATE_USER(UserExistsOption.NOT_EXISTS),

    /**
     * 비밀번호 변경 인증
     */
    RESET_PASSWORD(UserExistsOption.EXISTS),
}

enum class UserExistsOption {
    /**
     * 회원 정보가 있어야한다
     */
    EXISTS,

    /**
     * 회원 정보가 없어야한다
     */
    NOT_EXISTS,

    /**
     * 회원 정보의 유/무와 상관 없다
     */
    NONE
}