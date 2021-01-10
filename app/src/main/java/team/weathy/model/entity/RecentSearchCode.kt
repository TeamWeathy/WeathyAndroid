package team.weathy.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RecentSearchCode")
data class RecentSearchCode(@PrimaryKey val code: Int)