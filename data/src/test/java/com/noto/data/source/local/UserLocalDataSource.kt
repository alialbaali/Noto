package com.noto.data.source.local

import com.noto.domain.model.User

class FakeUserDao : UserLocalDataSource {

    private lateinit var pair: Pair<User, String>

    override fun createUser(user: User, userToken: String) {
        pair = Pair(user, userToken)
    }

    override fun getUserToken(): String {
        return pair.second
    }

    override fun getUser(): User {
        return pair.first
    }

}