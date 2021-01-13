package team.weathy.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "RecentSearchCode", indices = [Index(value = ["code"], unique = true)])
data class RecentSearchCode(val code: Long, @PrimaryKey(autoGenerate = true) val id: Int = 0)