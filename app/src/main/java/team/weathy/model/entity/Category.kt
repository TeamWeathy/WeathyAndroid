package team.weathy.model.entity

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.annotations.JsonAdapter
import team.weathy.api.ApiSerializer
import team.weathy.model.entity.Category.SAMPLE
import java.lang.reflect.Type

@JsonAdapter(CategorySerializer::class)
enum class Category(val value: Int) {
    SAMPLE(2)
}

class CategorySerializer : ApiSerializer<Category> {
    override fun serialize(src: Category, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.value)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Category {
        return Category.values().firstOrNull { it.value == json.asInt } ?: SAMPLE
    }
}