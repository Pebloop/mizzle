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

class EditorEntityInstance(val entity: EntityData, val actions: EditorActions) : Actor(),
    InputProcessor {

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
        setSize(50f, 50f)
        setScale(entity.transform.scale.x, entity.transform.scale.y)
        rotation = entity.transform.rotation
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
                if (dragged || entity.transform.position.dst(entity.transform.position.x + x,entity.transform.position.y + y) > 60) {
                    dragged = true
                    entity.transform.position.x += x
                    entity.transform.position.y += y
                    setPosition(entity.transform.position.x, entity.transform.position.y)
                }
            }
        })
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (actions.getSelectedEntity() == entity) {
            batch?.draw(
                yellowTexture,
                entity.transform.position.x - 2,
                entity.transform.position.y - 2,
                50f + 4,
                50f + 4
            )
        }
        batch?.draw(texture, entity.transform.position.x, entity.transform.position.y, 50f, 50f)
    }

    override fun keyDown(keycode: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun keyUp(keycode: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun keyTyped(character: Char): Boolean {
        TODO("Not yet implemented")
    }

    override fun touchDown(
        screenX: Int,
        screenY: Int,
        pointer: Int,
        button: Int
    ): Boolean {
        Gdx.app.debug("test", "tse2t")
        return true
    }

    override fun touchUp(
        screenX: Int,
        screenY: Int,
        pointer: Int,
        button: Int
    ): Boolean {
        Gdx.app.debug("test", "tset")
        if (screenX > entity.transform.position.x
            && screenX < entity.transform.position.x + 50
            && screenY > entity.transform.position.y
            && screenY < entity.transform.position.y + 50
        ) {
            if (actions.getSelectedEntity() == entity) {
                actions.selectEntity(null)
            } else {

                actions.selectEntity(entity)
            }
            return true
        }
        return false
    }

    override fun touchCancelled(
        screenX: Int,
        screenY: Int,
        pointer: Int,
        button: Int
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun touchDragged(
        screenX: Int,
        screenY: Int,
        pointer: Int
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        TODO("Not yet implemented")
    }


}
