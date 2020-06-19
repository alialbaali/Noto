package com.noto.domain.interactor.user

import com.noto.domain.repository.UserRepository

class GetUserToken(private val userRepository: UserRepository) {

    suspend operator fun invoke() = userRepository.getUserToken()

}
