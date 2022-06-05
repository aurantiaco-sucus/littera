package xyz.midnight233.dome41

import littera.Lifecycle
import xyz.midnight233.dome41.ch0p0.Introduction
import xyz.midnight233.littera.content.Artifact

fun registerDome41Artifacts() {
    Artifact.globalInstances += dome41Artifact
}

val dome41Artifact = Artifact(
    identity = "m233-dome41",
    title = "Dome 41",
    author = "Midnight233",
    description = "A bizarre adventure between the past and present, in domes.",
    version = 0,
    template = Lifecycle.profileTemplate(
        initialSegment = Introduction
    ),
    segments = listOf(
        Introduction
    ),
)