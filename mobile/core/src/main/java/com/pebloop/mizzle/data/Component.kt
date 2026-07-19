package com.pebloop.mizzle.data

import com.badlogic.gdx.graphics.g2d.Batch
import com.pebloop.mizzle.data.components.data.DataComponent
import java.io.Serializable

class Component(val component: ComponentData): Serializable {
    var datas: Map<String, DataComponent<*>> = mapOf<String, DataComponent<*>>()
    var name: String

    init {
        name = component.getDisplayName()
        datas = component.getDataList()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> setData(key: String, data: T) {
        (datas[key] as? DataComponent<T>)?.data = data
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

    fun getBounds(): com.badlogic.gdx.math.Vector2 {
        return component.getBounds(datas)
    }
}
