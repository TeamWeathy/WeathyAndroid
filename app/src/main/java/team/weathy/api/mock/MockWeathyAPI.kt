package team.weathy.api.mock

import retrofit2.Response
import team.weathy.api.CreateWeathyReq
import team.weathy.api.EditWeathyReq
import team.weathy.api.MessageRes
import team.weathy.api.WeathyAPI
import team.weathy.api.WeathyRes
import team.weathy.util.DateString
import javax.inject.Inject
import kotlin.random.Random

class MockWeathyAPI @Inject constructor() : WeathyAPI {
    override suspend fun fetchRecommendedWeathy(code: Long, date: DateString): Response<WeathyRes?> {
        return Response.success(WeathyRes(MockGenerator.weathy(), ""))
    }

    override suspend fun fetchWeathyWithDate(date: DateString): WeathyRes {
        return WeathyRes(if (Random.nextFloat() > 0.5f) MockGenerator.weathy() else null, "hi")
    }

    override suspend fun createWeathy(req: CreateWeathyReq): MessageRes {
        TODO("Not yet implemented")
    }

    override suspend fun editWeathy(weathyId: Int, req: EditWeathyReq): MessageRes {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeathy(weathyId: Int): MessageRes {
        TODO("Not yet implemented")
    }
}