package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent

class TextComponent : ComponentData {
    @Transient
    private var _layout: GlyphLayout? = null
    private val layout: GlyphLayout
        get() {
            if (_layout == null) {
                _layout = GlyphLayout()
            }
            return _layout!!
        }

    override fun getId(): String = "TEXT"

    override fun getDisplayName(): String = "Text"

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("text", DataComponent("Text", "STRING")),
            Pair("color", DataComponent<Int>(0xffffffff.toInt(), "COLOR")),
            Pair("scale", DataComponent(1.0f, "FLOAT")),
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
        val text = datas["text"]?.data as? String ?: ""
        val colorInt = datas["color"]?.data as? Int ?: 0xffffffff.toInt()
        val scale = datas["scale"]?.data as? Float ?: 1.0f

        val font = Main.getInstance().font
        val oldColor = font.color.cpy()
        val oldScaleX = font.data.scaleX
        val oldScaleY = font.data.scaleY

        val tempColor = com.badlogic.gdx.graphics.Color()
        com.badlogic.gdx.graphics.Color.argb8888ToColor(tempColor, colorInt)
        tempColor.a *= parentAlpha

        font.color = tempColor
        font.data.setScale(scale * scaleX, scale * scaleY)

        // Basic drawing. Rotation is not handled simply with font.draw without batch transform tweaks.
        // We use the top-left alignment for simplicity here.
        font.draw(batch, text, x, y + getBounds(datas).y)

        font.color = oldColor
        font.data.setScale(oldScaleX, oldScaleY)
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        val text = datas["text"]?.data as? String ?: ""
        val scale = datas["scale"]?.data as? Float ?: 1.0f
        val font = Main.getInstance().font
        val oldScaleX = font.data.scaleX
        val oldScaleY = font.data.scaleY
        font.data.setScale(scale, scale)
        layout.setText(font, text)
        val size = Vector2(layout.width, layout.height)
        font.data.setScale(oldScaleX, oldScaleY)
        return size
    }
}
