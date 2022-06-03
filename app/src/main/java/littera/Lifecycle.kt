package littera

import xyz.midnight233.littera.content.Segment

object Lifecycle {
    fun profileTemplate(
        initialSegment: Segment,
        vararg others: Pair<String, String>
    ) = mapOf(
        "Littera:Scene" to initialSegment::class.qualifiedName!!,
        *others
    )
}