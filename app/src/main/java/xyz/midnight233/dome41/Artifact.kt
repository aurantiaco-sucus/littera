package xyz.midnight233.dome41

import littera.Lifecycle
import xyz.midnight233.dome41.ch0.Intro
import xyz.midnight233.littera.content.Artifact

fun registerDome41Artifacts() {
    Artifact.globalInstances += dome41Artifact
}

val dome41Artifact = Artifact(
    identity = "m233-dome41",
    title = "Dome 41",
    author = "Midnight233",
    description = "A bizarre adventure between the past and present.",
    version = 0,
    template = Lifecycle.profileTemplate(
        initialScene = Intro
    ),
    scenes = listOf(
        Intro
    ),
)