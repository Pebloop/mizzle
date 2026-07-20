package com.pebloop.mizzle.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.pebloop.mizzle.data.EntityData

class EditorEntityInstance(val entity: EntityData, val actions: EditorActions) : Actor() {

    var pixmap: Pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    var yellow: Pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    var texture: Texture
    var yellowTexture: Texture
    var dragged: Boolean = false

    init {
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        texture = Texture(pixmap)
        yellow.setColor(Color.YELLOW)
        yellow.fill()
        yellowTexture = Texture(yellow)
        setPosition(entity.transform.position.x, entity.transform.position.y)
        updateSize()
        setScale(entity.transform.scale.x, entity.transform.scale.y)
        rotation = entity.transform.rotation
        zIndex = entity.transform.zIndex
        addListener(object : InputListener() {
            override fun touchUp(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ) {
                super.touchUp(event, x, y, pointer, button)
                if (dragged) {
                    dragged = false
                } else {
                    if (actions.getSelectedEntity() == entity) {
                        actions.selectEntity(null)
                    } else {
                        actions.selectEntity(entity)
                    }
                }
            }

            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                val dx = x - width / 2f
                val dy = y - height / 2f
                if (dragged || Vector2(dx, dy).len() > 20) {
                    dragged = true
                    val stagePos = localToStageCoordinates(Vector2(x, y))

                    when (actions.getInteractionMode()) {
                        EditorScreen.InteractionMode.MOVE -> {
                            entity.transform.position.x = stagePos.x - width / 2f
                            entity.transform.position.y = stagePos.y - height / 2f
                            setPosition(entity.transform.position.x, entity.transform.position.y)
                        }
                        EditorScreen.InteractionMode.ROTATE -> {
                            val center = localToStageCoordinates(Vector2(width / 2f, height / 2f))
                            val angle = Vector2(stagePos.x - center.x, stagePos.y - center.y).angleDeg()
                            entity.transform.rotation = angle
                            rotation = angle
                        }
                        EditorScreen.InteractionMode.SCALE -> {
                            val center = localToStageCoordinates(Vector2(width / 2f, height / 2f))
                            val dist = Vector2(stagePos.x - center.x, stagePos.y - center.y).len()
                            val scale = dist / (Vector2(width / 2f, height / 2f).len())
                            entity.transform.scale.x = scale
                            entity.transform.scale.y = scale
                            setScale(scale, scale)
                        }
                    }
                }
            }
        })
    }

    fun forceUpdate() {
        setPosition(entity.transform.position.x, entity.transform.position.y)
        updateSize()
        setScale(entity.transform.scale.x, entity.transform.scale.y)
        rotation = entity.transform.rotation
        zIndex = entity.transform.zIndex
    }

    override fun act(delta: Float) {
        super.act(delta)
        entity.components.forEach { it.update(delta, entity) }
    }

    private fun updateSize() {
        var maxWidth = 0f
        var maxHeight = 0f
        for (component in entity.components) {
            val bounds = component.getBounds()
            if (bounds.x > maxWidth) maxWidth = bounds.x
            if (bounds.y > maxHeight) maxHeight = bounds.y
        }
        setSize(maxWidth, maxHeight)
        setOrigin(maxWidth / 2f, maxHeight / 2f)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (actions.getSelectedEntity() == entity) {
            batch?.draw(
                yellowTexture,
                getX() - 2,
                getY() - 2,
                getOriginX() + 2,
                getOriginY() + 2,
                getWidth() + 4,
                getHeight() + 4,
                getScaleX(),
                getScaleY(),
                getRotation(),
                0,
                0,
                1,
                1,
                false,
                false
            )
        }
        for (component in entity.components) {
            component.draw(
                batch,
                parentAlpha,
                getX(),
                getY(),
                getOriginX(),
                getOriginY(),
                getScaleX(),
                getScaleY(),
                getRotation()
            )
        }
    }
}
