package team.weathy.model.entity

import com.google.gson.annotations.SerializedName

data class Clothes(
    @SerializedName("id") val id: Int,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("name") val name: String
)