package xyz.midnight233.littera.persist

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Profile::class,
        Prefs::class],
    version = 1,
    exportSchema = false)
abstract class LitteraBase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun prefsDao(): PrefsDao
}