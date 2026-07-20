package com.pebloop.mizzle.event_builder

import java.io.Serializable

data class PuzzlePieceData(
    val type: String,
    val x: Float,
    val y: Float,
    var next: PuzzlePieceData? = null,
    val internalValues: MutableMap<Int, PuzzlePieceData?> = mutableMapOf(),
    val customValues: MutableMap<Int, String> = mutableMapOf(),
    var body: PuzzlePieceData? = null
) : Serializable
