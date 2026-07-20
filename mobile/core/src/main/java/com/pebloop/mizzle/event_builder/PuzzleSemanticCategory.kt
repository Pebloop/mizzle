package com.pebloop.mizzle.event_builder

import com.badlogic.gdx.graphics.Color

enum class PuzzleSemanticCategory(
    val displayName: String,
    val color: Color
) {
    ENTITY("Entity", Color(0.2f, 0.4f, 0.8f, 1f)),
    NUMBERS("Numbers", Color(0.5f, 0.2f, 0.7f, 1f)),
    TEXT("Text", Color(0.2f, 0.6f, 0.2f, 1f)),
    LOGIC("Logic", Color(0.8f, 0.4f, 0.1f, 1f))
}
