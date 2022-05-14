package xyz.midnight233.littera.persist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import xyz.midnight233.littera.struct.Artifact

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile")
    fun everything(): List<Profile>

    @Query("SELECT * FROM profile WHERE artifact_identity LIKE :artifactIdentity")
    fun forArtifactIdentity(artifactIdentity: String): List<Profile>

    fun forArtifact(artifact: Artifact) = forArtifactIdentity(artifact.identity)

    @Insert
    fun insert(profile: Profile)

    @Delete
    fun delete(profile: Profile)
}