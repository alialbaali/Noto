package com.noto.app.data.model.remote

sealed class ResponseException : RuntimeException() {

    operator fun invoke(): Nothing = throw this

    sealed class Auth : ResponseException() {
        object UserAlreadyRegistered : Auth()
        object InvalidEmail : Auth()
        object InvalidPassword : Auth()
        object InvalidLoginCredentials : Auth()
        object InvalidRefreshToken : Auth()
    }

    object NetworkErrorException : ResponseException()

}