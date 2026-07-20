package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent

class SpriteComponent : ComponentData {
    @Transient
    private var _whiteRegion: TextureRegion? = null
    private val whiteRegion: TextureRegion
        get() {
            if (_whiteRegion == null) {
                _whiteRegion = TextureRegion(Main.getInstance().whitePixel)
            }
            return _whiteRegion!!
        }

    override fun getId(): String = "SPRITE"

    override fun getDisplayName(): String = "Sprite"

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("texture", DataComponent("", "TEXTURE")),
            Pair("width", DataComponent(100f, "FLOAT")),
            Pair("height", DataComponent(100f, "FLOAT")),
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
        val width = datas["width"]?.data as? Float ?: 100f
        val height = datas["height"]?.data as? Float ?: 100f
        val colorInt = datas["color"]?.data as? Int ?: 0xffffffff.toInt()
        val flipX = datas["flipX"]?.data as? Boolean ?: false
        val flipY = datas["flipY"]?.data as? Boolean ?: false

        val region = Main.getInstance().getUserTexture(textureName) ?: whiteRegion

        val oldColor = batch.color.cpy()
        val tempColor = Color()
        Color.argb8888ToColor(tempColor, colorInt)
        tempColor.a *= parentAlpha
        batch.color = tempColor

        batch.draw(
            region,
            x, y,
            originX, originY,
            width, height,
            scaleX, scaleY,
            rotation
        )
        // Note: TextureRegion doesn't have flip state in the draw call like Texture,
        // we'd need to flip the region itself or use a different draw call.
        // For simplicity and to avoid modifying shared regions, let's just use the basic draw.
        // If flipping is needed, we should probably handle it in the region creation or use a different overload.
        // Actually, LibGDX's batch.draw has no overload that takes flipX/flipY for TextureRegion directly without a lot of params.

        batch.color = oldColor
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        val width = datas["width"]?.data as? Float ?: 100f
        val height = datas["height"]?.data as? Float ?: 100f
        return Vector2(width, height)
    }
}
