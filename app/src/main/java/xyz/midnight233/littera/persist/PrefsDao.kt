package xyz.midnight233.littera.persist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PrefsDao {
    @Query("SELECT * from prefs")
    fun prefs(): List<Prefs>

    @Insert
    fun insert(prefs: Prefs)

    @Delete
    fun delete(prefs: Prefs)
}