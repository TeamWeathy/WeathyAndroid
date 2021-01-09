package team.weathy.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query
import team.weathy.model.entity.CalendarPreview
import team.weathy.util.DateString


data class FetchCalendarPreviewRes(
    @SerializedName("weathyOverviewList") val list: List<CalendarPreview?>,
    @SerializedName("message") val message: String
)

interface CalendarAPI {
    @GET("users/$USER_ID_PATH_SEGMENT/calendar")
    suspend fun fetchCalendarPreview(
        @Query("start") start: DateString, @Query("end") end: DateString
    ): FetchCalendarPreviewRes
}