package xyz.midnight233.littera.stateful

data class NotebookEntry(
    val title: String,
    val type: NotebookEntryType,
    val content: String
)

enum class NotebookEntryType {
    Todo, Plot, Memo, Idea
}