package com.pebloop.mizzle.data.components

import java.io.Serializable

data class SpriteAnimationData(
    var name: String = "new_animation",
    var textures: List<String> = mutableListOf(),
    var frameDuration: Float = 0.1f,
    var looping: Boolean = true
) : Serializable
