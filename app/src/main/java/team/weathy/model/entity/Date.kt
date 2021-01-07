package team.weathy.model.entity

import com.google.gson.annotations.SerializedName

data class Date(
    @SerializedName("month") val month: Int,
    @SerializedName("day") val day: Int,
    @SerializedName("dayOfWeek") val dayOfWeek: String
)