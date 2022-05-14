package xyz.midnight233.littera.stateful

data class ActionEntry(
    val name: String,
    val type: ActionEntryType,
    val description: String,
)

enum class ActionEntryType {
    Context, Interact, Backpack, Misc
}