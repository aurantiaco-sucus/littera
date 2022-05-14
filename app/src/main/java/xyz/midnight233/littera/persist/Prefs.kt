package xyz.midnight233.littera.persist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Prefs(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "owner_name") val ownerName: String
)