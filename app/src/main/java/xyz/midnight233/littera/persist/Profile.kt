package xyz.midnight233.littera.persist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.midnight233.littera.runtime.Runtime
import xyz.midnight233.littera.stateful.JournalEntry
import xyz.midnight233.littera.stateful.JournalEntryType
import java.lang.StringBuilder
import kotlin.reflect.KProperty

@Entity
data class Profile(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "data_keys") val dataKeys: String,
    @ColumnInfo(name = "data_values") val dataValues: String,
    @ColumnInfo(name = "journal") val journal: String,
    @ColumnInfo(name = "artifact_identity") val artifactIdentity: String
) {
    companion object {
        lateinit var instance: Profile
        private var cacheLazy: Lazy<MutableMap<String, String>> = lazy {
            instance.dataKeys
                .lineSequence()
                .zip(instance.dataValues
                    .lineSequence())
                .toMap()
                .toMutableMap()
        }
        private val cache by cacheLazy

        operator fun get(key: String): String? = cache[key]
        operator fun set(key: String, value: String) { cache[key] = value }

        fun pushProfile() {
            Runtime.state.litteraBase.profileDao().delete(instance)
            instance = instance.copy(
                dataKeys = cache.keys.joinToString("\n"),
                dataValues = cache.values.joinToString("\n")
            )
            Runtime.state.litteraBase.profileDao().insert(instance)
        }

        inline fun <reified TSegmentObject> scene() =
            ProfileDelegator(TSegmentObject::class.qualifiedName!!)

        fun scene(segmentObjectTypeName: String) =
            ProfileDelegator(segmentObjectTypeName)

        class ProfileDelegator(val scene: String) {
            fun mark(name: String) = NavNamedDelegate(
                path = "Segment(${scene}):Mark(${name})",
                fromString = { it.toBoolean() },
                toString = { it.toString() }
            )

            fun memo(name: String) = NavNamedDelegate(
                path = "Segment(${scene}):Memo(${name})",
                fromString = { it },
                toString = { it }
            )

            fun markDeferred(name: () -> String) = NavDeferredNamedDelegate(
                pathDeferred = { "Segment(${scene}):Mark(${name()})" },
                fromString = { it.toBoolean() },
                toString = { it.toString() }
            )

            fun memoDeferred(name: () -> String) = NavDeferredNamedDelegate(
                pathDeferred = { "Segment(${scene}):Memo(${name()})" },
                fromString = { it },
                toString = { it }
            )

            val markDelegate = NavPropDelegate(
                pathModel = "Segment(${scene}):Mark(<prop>)",
                fromString = { it.toBoolean() },
                toString = { it.toString() }
            )

            val memoDelegate = NavPropDelegate(
                pathModel = "Segment(${scene}):Memo(<prop>)",
                fromString = { it },
                toString = { it }
            )
        }

        class NavNamedDelegate<TContent>(
            val path: String,
            private val fromString: (String) -> TContent,
            private val toString: (TContent) -> String
        ) {
            operator fun getValue(thisRef: Any?, property: KProperty<*>): TContent =
                fromString(cache[path].orEmpty())
            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TContent) {
                cache[path] = toString(value)
            }
        }

        class NavDeferredNamedDelegate<TContent>(
            pathDeferred: () -> String,
            private val fromString: (String) -> TContent,
            private val toString: (TContent) -> String
        ) {
            private val path by lazy(pathDeferred)
            operator fun getValue(thisRef: Any?, property: KProperty<*>): TContent =
                fromString(cache[path].orEmpty())
            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TContent) {
                cache[path] = toString(value)
            }
        }

        class NavPropDelegate<TContent>(
            val pathModel: String,
            private val fromString: (String) -> TContent,
            private val toString: (TContent) -> String
        ) {
            operator fun getValue(thisRef: Any?, property: KProperty<*>): TContent =
                fromString(cache[pathModel.replace("<prop>", property.name)].orEmpty())
            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: TContent) {
                cache[pathModel.replace("<prop>", property.name)] = toString(value)
            }
        }

        var currentSceneValue: String
        get() = this["Littera:Scene"]!!
        set(value) { this["Littera:Scene"] = value }

        fun pullJournal(): List<JournalEntry> {
            val lines = instance.journal.lines()
            val types = lines[0]
                .split(";")
            val contents = lines.drop(1)
                .map { it
                    .split(";")
                    .map { it
                        .replace("<<sc>>", ";")
                        .replace("<<cr>>", "\r")
                        .replace("<<lf>>", "\n") } }
            return types.zip(contents)
                .map { (ty, c) ->
                    JournalEntry(
                        type = JournalEntryType.valueOf(ty),
                        contents = c) }
        }

        fun pushJournal(journal: List<JournalEntry>) {
            Runtime.state.litteraBase.profileDao().delete(instance)
            instance = instance.copy(
                journal = StringBuilder()
                    .append(journal
                        .joinToString(";") { it.type.name })
                    .append("\n")
                    .append(journal
                        .map { it.contents
                            .map { it
                                .replace("\n", "<<lf>>")
                                .replace("\r", "<<cr>>")
                                .replace(";", "<<sc>>") }
                            .joinToString(";") }
                        .joinToString("\n"))
                    .toString())
            Runtime.state.litteraBase.profileDao().insert(instance)
        }
    }
}