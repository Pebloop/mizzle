package com.pebloop.mizzle.data

import com.badlogic.gdx.math.Vector2
import java.io.Serializable

class Transform: Serializable {
    var position: Vector2 = Vector2(0f,0f)
    var rotation: Float = 0f
    var scale: Vector2 = Vector2(1f,1f)
    var zIndex: Int = 0
}
