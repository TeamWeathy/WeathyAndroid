package team.weathy.api.mock

import team.weathy.api.CreateUserReq
import team.weathy.api.EditUserReq
import team.weathy.api.UserAPI
import team.weathy.api.UserRes
import team.weathy.model.entity.User
import javax.inject.Inject

class MockUserAPI @Inject constructor() : UserAPI {
    override suspend fun createUser(req: CreateUserReq): UserRes {
        return UserRes(User(1, req.nickname), "token", "message")
    }

    override suspend fun editUser(req: EditUserReq): UserRes {
        return UserRes(User(1, req.nickname), "token", "message")
    }
}