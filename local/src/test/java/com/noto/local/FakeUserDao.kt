package com.noto.local

import com.noto.data.source.local.UserLocalDataSource
import com.noto.domain.model.User

class FakeUserDao(private val users: MutableMap<User, String>) : UserLocalDataSource {

    override fun createUser(user: User, userToken: String) {
        users[user] = userToken
    }

    override fun getUserToken(): String {
        return users.values.first()
    }

    override fun getUser(): User {
        return users.keys.first()
    }

}