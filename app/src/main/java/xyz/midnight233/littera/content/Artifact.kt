package xyz.midnight233.littera.content

import xyz.midnight233.littera.persist.Profile

class Artifact(
    val identity: String,
    val title: String,
    val author: String,
    val description: String,
    val version: Int,
    val template: Map<String, String>,
    val segments: List<Segment>,
) {
    fun createProfile(name: String) = Profile(
        id = (name + identity).hashCode(),
        name = name,
        dataKeys = template.keys.toList().joinToString("\n"),
        dataValues = template.values.toList().joinToString("\n"),
        artifactIdentity = identity,
        journal = "Notification\nBegin of journal..."
    )

    companion object {
        val globalInstances = mutableListOf<Artifact>()
    }
}