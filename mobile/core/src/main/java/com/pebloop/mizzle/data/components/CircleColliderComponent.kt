package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent

class CircleColliderComponent : ComponentData {
    override fun getId(): String = "CIRCLE_COLLIDER"

    override fun getDisplayName(): String = "Circle Collider"

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("radius", DataComponent<Float>(25f, "FLOAT"))
        )
    }

    override fun draw(
        batch: Batch?,
        parentAlpha: Float,
        x: Float,
        y: Float,
        originX: Float,
        originY: Float,
        scaleX: Float,
        scaleY: Float,
        rotation: Float,
        datas: Map<String, DataComponent<*>>
    ) {
        // Colliders are logic components, they don't draw anything normally.
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        val radius = datas["radius"]?.data as? Float ?: 0f
        return Vector2(radius * 2, radius * 2)
    }
}
