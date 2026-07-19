package com.pebloop.mizzle.player

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.pebloop.mizzle.data.EntityData

class GameEntityInstance(val entity: EntityData) : Actor() {

    init {
        setPosition(entity.transform.position.x, entity.transform.position.y)
        updateSize()
        setScale(entity.transform.scale.x, entity.transform.scale.y)
        rotation = entity.transform.rotation
        zIndex = entity.transform.zIndex
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
