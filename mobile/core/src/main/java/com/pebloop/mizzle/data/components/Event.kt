package com.pebloop.mizzle.data.components

import com.pebloop.mizzle.event_builder.PuzzlePieceData
import java.io.Serializable

class Event(var code: String = "none") : Serializable {
    var pieces: List<PuzzlePieceData> = mutableListOf()
}
