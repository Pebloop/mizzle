package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent
import com.pebloop.mizzle.util.Graphics

class CircleComponent : ComponentData {
    override fun getId(): String {
        return "CIRCLE"
    }

    override fun getDisplayName(): String {
        return "Circle"
    }

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("radius", DataComponent<Float>(25f, "FLOAT")),
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
        val radius = datas["radius"]?.data as? Float ?: 0f
        val colorInt = datas["color"]?.data as? Int ?: 0xffffffff.toInt()
        val outlineColorInt = datas["outline color"]?.data as? Int ?: 0xffffffff.toInt()
        val outlineWidth = datas["outline width"]?.data as? Int ?: 0

        val oldColor = batch.color.cpy()
        val tempColor = com.badlogic.gdx.graphics.Color()
        val circleTexture = Graphics.circle

        // Draw outline if needed
        if (outlineWidth > 0) {
            com.badlogic.gdx.graphics.Color.argb8888ToColor(tempColor, outlineColorInt)
            tempColor.a *= parentAlpha
            batch.color = tempColor
            val totalRadius = radius + outlineWidth
            val diameter = totalRadius * 2
            // Center the larger circle relative to the original radius
            val offset = outlineWidth.toFloat()
            batch.draw(circleTexture, x - offset, y - offset, originX + offset, originY + offset, diameter, diameter, scaleX, scaleY, rotation, 0, 0, circleTexture.width, circleTexture.height, false, false)
        }

        // Fill
        com.badlogic.gdx.graphics.Color.argb8888ToColor(tempColor, colorInt)
        tempColor.a *= parentAlpha
        batch.color = tempColor
        val diameter = radius * 2
        batch.draw(circleTexture, x, y, originX, originY, diameter, diameter, scaleX, scaleY, rotation, 0, 0, circleTexture.width, circleTexture.height, false, false)

        batch.color = oldColor
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        val radius = datas["radius"]?.data as? Float ?: 0f
        return Vector2(radius * 2, radius * 2)
    }
}
