package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent

class RigidBodyComponent : ComponentData {
    override fun getId(): String = "RIGID_BODY"

    override fun getDisplayName(): String = "Rigid Body"

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("isDynamic", DataComponent<Boolean>(true, "BOOLEAN")),
            Pair("mass", DataComponent<Float>(1.0f, "FLOAT")),
            Pair("friction", DataComponent<Float>(0.2f, "FLOAT")),
            Pair("restitution", DataComponent<Float>(0.0f, "FLOAT"))
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
        // RigidBody is a logic component, it doesn't draw anything.
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        return Vector2(0f, 0f)
    }
}
