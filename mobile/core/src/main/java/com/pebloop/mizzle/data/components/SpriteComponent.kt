package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent

class SpriteComponent : ComponentData {
    override fun getId(): String = "SPRITE"

    override fun getDisplayName(): String = "Sprite"

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("texture", DataComponent("", "STRING")),
            Pair("color", DataComponent(0xffffffff.toInt(), "COLOR")),
            Pair("flipX", DataComponent(false, "BOOLEAN")),
            Pair("flipY", DataComponent(false, "BOOLEAN"))
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
        val textureName = datas["texture"]?.data as? String ?: ""
        val colorInt = datas["color"]?.data as? Int ?: 0xffffffff.toInt()
        val flipX = datas["flipX"]?.data as? Boolean ?: false
        val flipY = datas["flipY"]?.data as? Boolean ?: false

        val texture = Main.getInstance().getUserTexture(textureName) ?: Main.getInstance().whitePixel

        val oldColor = batch.color.cpy()
        val tempColor = Color()
        Color.argb8888ToColor(tempColor, colorInt)
        tempColor.a *= parentAlpha
        batch.color = tempColor

        batch.draw(
            texture,
            x, y,
            originX, originY,
            texture.width.toFloat(), texture.height.toFloat(),
            scaleX, scaleY,
            rotation,
            0, 0,
            texture.width, texture.height,
            flipX, flipY
        )

        batch.color = oldColor
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        val textureName = datas["texture"]?.data as? String ?: ""
        val texture = Main.getInstance().getUserTexture(textureName) ?: Main.getInstance().whitePixel
        return Vector2(texture.width.toFloat(), texture.height.toFloat())
    }
}
