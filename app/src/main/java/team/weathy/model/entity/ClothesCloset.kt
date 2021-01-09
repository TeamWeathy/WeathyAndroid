package team.weathy.model.entity

data class ClothesCloset(
    val top: List<Clothes>, val bottom: List<Clothes>, val outer: List<Clothes>, val etc: List<Clothes>
)