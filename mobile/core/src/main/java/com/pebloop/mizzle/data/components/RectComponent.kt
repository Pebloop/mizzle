package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent
import com.pebloop.mizzle.data.util.SerializableVector2

class RectComponent: ComponentData {
    override fun getId(): String {
        return "RECT"
    }

    override fun getDisplayName(): String {
        return "Rectangle"
    }

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("size", DataComponent<SerializableVector2>(SerializableVector2(50f,50f), "VECTOR2")),
            Pair("color", DataComponent<Int>(0xffffffff.toInt(), "COLOR")),
            Pair("outline color", DataComponent<Int>(0xffffffff.toInt(), "COLOR")),
            Pair("outline width", DataComponent<Int>(0, "INT"))
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
        if (batch == null) return
        val size = (datas["size"]?.data as? SerializableVector2)?.toVector2() ?: Vector2(0f, 0f)
        val colorInt = datas["color"]?.data as? Int ?: 0xffffffff.toInt()
        val outlineColorInt = datas["outline color"]?.data as? Int ?: 0xffffffff.toInt()
        val outlineWidth = datas["outline width"]?.data as? Int ?: 0

        val oldColor = batch.color.cpy()
        val tempColor = com.badlogic.gdx.graphics.Color()
        val white = com.pebloop.mizzle.Main.getInstance().whitePixel

        // Fill
        com.badlogic.gdx.graphics.Color.argb8888ToColor(tempColor, colorInt)
        tempColor.a *= parentAlpha
        batch.color = tempColor
        batch.draw(white, x, y, originX, originY, size.x, size.y, scaleX, scaleY, rotation, 0, 0, 1, 1, false, false)

        if (outlineWidth > 0) {
            com.badlogic.gdx.graphics.Color.argb8888ToColor(tempColor, outlineColorInt)
            tempColor.a *= parentAlpha
            batch.color = tempColor
            val fOutlineWidth = outlineWidth.toFloat()

            // Bottom
            batch.draw(white, x, y, originX, originY, size.x, fOutlineWidth, scaleX, scaleY, rotation, 0, 0, 1, 1, false, false)
            // Top
            val topY = y + size.y - fOutlineWidth
            batch.draw(white, x, topY, originX, originY - (size.y - fOutlineWidth), size.x, fOutlineWidth, scaleX, scaleY, rotation, 0, 0, 1, 1, false, false)
            // Left
            batch.draw(white, x, y, originX, originY, fOutlineWidth, size.y, scaleX, scaleY, rotation, 0, 0, 1, 1, false, false)
            // Right
            val rightX = x + size.x - fOutlineWidth
            batch.draw(white, rightX, y, originX - (size.x - fOutlineWidth), originY, fOutlineWidth, size.y, scaleX, scaleY, rotation, 0, 0, 1, 1, false, false)
        }

        batch.color = oldColor
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        return (datas["size"]?.data as? SerializableVector2)?.toVector2() ?: Vector2(0f, 0f)
    }
}
