package com.claudijusapchy.ratprotection.features

object FeatureManager {

    private val features = listOf(
        PartyJoinSoundFeature
        // add more features here later
    )

    fun init() {
        features.forEach { feature ->
            if (feature.isEnabled()) {
                feature.onActivate()
            }
        }
    }
}