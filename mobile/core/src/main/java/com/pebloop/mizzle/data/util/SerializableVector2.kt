package com.pebloop.mizzle.data.util

import com.badlogic.gdx.math.Vector2
import java.io.Serializable

data class SerializableVector2(var x: Float = 0f, var y: Float = 0f) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }

    fun toVector2(): Vector2 = Vector2(x, y)

    fun set(v: Vector2): SerializableVector2 {
        this.x = v.x
        this.y = v.y
        return this
    }
}

fun Vector2.toSerializable(): SerializableVector2 = SerializableVector2(x, y)
