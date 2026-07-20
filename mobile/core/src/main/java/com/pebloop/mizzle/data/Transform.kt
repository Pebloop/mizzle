package com.pebloop.mizzle.data

import com.pebloop.mizzle.data.util.SerializableVector2
import java.io.Serializable

class Transform: Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }

    var position: SerializableVector2 = SerializableVector2(0f,0f)
    var rotation: Float = 0f
    var scale: SerializableVector2 = SerializableVector2(1f,1f)
    var zIndex: Int = 0
}
