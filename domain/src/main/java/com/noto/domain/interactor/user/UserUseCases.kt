package com.noto.domain.interactor.user

import com.noto.domain.interactor.FetchData

class UserUseCases(
    val createUser: CreateUser,
    val loginUser: LoginUser,
    val getUserToken: GetUserToken,
    val fetchData: FetchData
)