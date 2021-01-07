package team.weathy.api.mock

import team.weathy.model.entity.User

object MockGenerator {
    fun user() = User(1, "mock user")
}