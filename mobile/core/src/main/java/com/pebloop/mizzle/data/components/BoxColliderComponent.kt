package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent
import com.pebloop.mizzle.data.util.SerializableVector2

class BoxColliderComponent : ComponentData {
    override fun getId(): String = "BOX_COLLIDER"

    override fun getDisplayName(): String = "Box Collider"

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("size", DataComponent<SerializableVector2>(SerializableVector2(50f, 50f), "VECTOR2"))
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
        // We could add debug drawing here if needed.
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        return (datas["size"]?.data as? SerializableVector2)?.toVector2() ?: Vector2(0f, 0f)
    }
}
