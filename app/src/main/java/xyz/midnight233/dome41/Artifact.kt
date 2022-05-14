package xyz.midnight233.dome41

import littera.Lifecycle
import xyz.midnight233.littera.struct.Artifact

fun registerDome41Artifacts() {
    Artifact.globalInstances += dome41Artifact
}

val dome41Artifact = Artifact(
    identity = "m233-dome41",
    title = "Dome 41",
    author = "Midnight233",
    description = "Dome 41 is a game about a mysterious place.",
    version = 0,
    template = Lifecycle.profileTemplate(
        initialScene = SceneTest
    ),
    scenes = listOf(
        SceneTest
    ),
)