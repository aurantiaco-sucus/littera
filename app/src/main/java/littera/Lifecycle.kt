package littera

import xyz.midnight233.littera.content.Scene

object Lifecycle {
    fun profileTemplate(
        initialScene: Scene,
        vararg others: Pair<String, String>
    ) = mapOf(
        "Littera:Scene" to initialScene::class.qualifiedName!!,
        *others
    )
}