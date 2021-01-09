package team.weathy.model.entity

data class WeathyClothes(
    val categoryId: Int, val clothes: List<Cloth>
) {
    data class Cloth(
        val id: Int, val name: String
    )
}