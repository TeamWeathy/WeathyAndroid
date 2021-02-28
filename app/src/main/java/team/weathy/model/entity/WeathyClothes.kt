package team.weathy.model.entity

data class WeathyClothes(
    val categoryId: ClothCategory, val clothesNum: Int, val clothes: List<WeathyCloth>
) {}