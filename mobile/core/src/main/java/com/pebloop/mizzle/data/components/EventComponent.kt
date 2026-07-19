package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent

class EventComponent : ComponentData {
    override fun getId(): String = "EVENT"

    override fun getDisplayName(): String = "Event"

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("event", DataComponent<EventType>(EventType.ON_CLICK, "EVENT_TYPE")),
            Pair("action", DataComponent<Event>(Event("none"), "EVENT"))
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
        // Event components are logic-only, no drawing.
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        return Vector2(0f, 0f)
    }
}
