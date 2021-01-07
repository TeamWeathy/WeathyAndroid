package team.weathy.model.entity

import com.google.gson.annotations.SerializedName

data class Climate(
    @SerializedName("iconId") val iconId: Int, @SerializedName("description") val description: String
)