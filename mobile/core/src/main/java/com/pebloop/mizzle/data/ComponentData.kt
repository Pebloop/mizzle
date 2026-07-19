package com.pebloop.mizzle.data

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.data.components.data.DataComponent
import java.io.Serializable

interface ComponentData: Serializable {

    fun getId(): String

    fun getDisplayName(): String

    fun getDataList(): Map<String, DataComponent<*>>

    fun draw(
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
    )

    fun getBounds(datas: Map<String, DataComponent<*>>): Vector2
}
