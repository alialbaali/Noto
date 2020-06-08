package com.noto.domain.interactor.user

import com.noto.domain.model.User
import com.noto.domain.repository.UserRepository

class CreateUser(private val userRepository: UserRepository) {

    suspend operator fun invoke(user: User) = userRepository.createUser(user)

}