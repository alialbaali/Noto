package com.noto.domain.interactor.user

import com.noto.domain.model.User
import com.noto.domain.repository.UserRepository

class LoginUser(private val userRepository: UserRepository) {

    suspend operator fun invoke(user: User) = userRepository.loginUser(user)

}