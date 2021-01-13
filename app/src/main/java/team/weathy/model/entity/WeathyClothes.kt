package team.weathy.model.entity

data class WeathyClothes(
    val categoryId: ClothCategory, val clothes: List<WeathyCloth>
) {}