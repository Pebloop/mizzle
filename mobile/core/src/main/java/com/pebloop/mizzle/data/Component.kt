package com.pebloop.mizzle.data

import com.badlogic.gdx.graphics.g2d.Batch
import com.pebloop.mizzle.data.components.RectComponent
import com.pebloop.mizzle.data.components.data.DataComponent
import java.io.Serializable

class Component(val component: ComponentData = RectComponent()): Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }

    var datas: Map<String, DataComponent<*>> = mapOf<String, DataComponent<*>>()
    var name: String

    init {
        name = component.getDisplayName()
        datas = component.getDataList()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> setData(key: String, data: T) {
        val dataComponent = datas[key] as? DataComponent<T> ?: return

        // Type safety check
        if (data != null) {
            val currentData = dataComponent.data
            if (currentData != null && !currentData::class.java.isInstance(data)) {
                // If we are trying to set a float to a Vector2 field (common in animations)
                // we should probably handle it or at least prevent the crash.
                // But the AnimationComponent will be updated to handle this correctly.
                return
            }
        }

        dataComponent.data = data
    }

    fun getData(key: String): Any? {
        return datas[key]?.data
    }

    fun draw(
        batch: Batch?,
        parentAlpha: Float,
        x: Float,
        y: Float,
        originX: Float,
        originY: Float,
        scaleX: Float,
        scaleY: Float,
        rotation: Float
    ) {
        component.draw(batch, parentAlpha, x, y, originX, originY, scaleX, scaleY, rotation, datas)
    }

    fun update(delta: Float, entity: EntityData) {
        component.update(delta, entity, datas)
    }

    fun getBounds(): com.badlogic.gdx.math.Vector2 {
        return component.getBounds(datas)
    }
}
