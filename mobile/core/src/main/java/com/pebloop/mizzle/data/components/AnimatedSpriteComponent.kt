package com.pebloop.mizzle.data.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.components.data.DataComponent

class AnimatedSpriteComponent : ComponentData {
    @Transient
    private var _whiteRegion: TextureRegion? = null
    private val whiteRegion: TextureRegion
        get() {
            if (_whiteRegion == null) {
                _whiteRegion = TextureRegion(Main.getInstance().whitePixel)
            }
            return _whiteRegion!!
        }

    override fun getId(): String = "ANIMATED_SPRITE"

    override fun getDisplayName(): String = "Animated Sprite"

    override fun getDataList(): Map<String, DataComponent<*>> {
        return mapOf(
            Pair("animations", DataComponent(ArrayList<SpriteAnimationData>(), "ANIMATIONS")),
            Pair("currentAnimation", DataComponent("", "STRING")),
            Pair("stateTime", DataComponent(0f, "FLOAT")),
            Pair("width", DataComponent(100f, "FLOAT")),
            Pair("height", DataComponent(100f, "FLOAT")),
            Pair("color", DataComponent(0xffffffff.toInt(), "COLOR")),
            Pair("flipX", DataComponent(false, "BOOLEAN")),
            Pair("flipY", DataComponent(false, "BOOLEAN"))
        )
    }

    override fun update(delta: Float, entity: com.pebloop.mizzle.data.EntityData, datas: Map<String, DataComponent<*>>) {
        val stateTime = datas["stateTime"]?.data as? Float ?: 0f
        (datas["stateTime"] as? DataComponent<Float>)?.data = stateTime + delta
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
        val animations = datas["animations"]?.data as? ArrayList<SpriteAnimationData> ?: return
        val currentAnimationName = datas["currentAnimation"]?.data as? String ?: ""
        val stateTime = datas["stateTime"]?.data as? Float ?: 0f
        val width = datas["width"]?.data as? Float ?: 100f
        val height = datas["height"]?.data as? Float ?: 100f
        val colorInt = datas["color"]?.data as? Int ?: 0xffffffff.toInt()
        val flipX = datas["flipX"]?.data as? Boolean ?: false
        val flipY = datas["flipY"]?.data as? Boolean ?: false

        val animation = animations.find { it.name == currentAnimationName } ?: animations.firstOrNull() ?: return
        if (animation.textures.isEmpty()) return

        val frameCount = animation.textures.size
        var frameIndex = (stateTime / animation.frameDuration).toInt()

        if (animation.looping) {
            frameIndex %= frameCount
        } else {
            frameIndex = Math.min(frameIndex, frameCount - 1)
        }

        val textureName = animation.textures[frameIndex]
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

        batch.color = oldColor
    }

    override fun getBounds(datas: Map<String, DataComponent<*>>): Vector2 {
        val width = datas["width"]?.data as? Float ?: 100f
        val height = datas["height"]?.data as? Float ?: 100f
        return Vector2(width, height)
    }
}
