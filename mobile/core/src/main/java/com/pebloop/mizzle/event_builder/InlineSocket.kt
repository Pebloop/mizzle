package com.pebloop.mizzle.event_builder

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.pebloop.mizzle.editor.EditorSkin
import com.pebloop.mizzle.util.Graphics

class InlineSocket(val index: Int, val expectedType: PuzzleValueType, val skin: EditorSkin) : WidgetGroup() {
    private var texture: Texture? = null
    var attachedPiece: PuzzlePiece? = null

    private val notchRadius = 20

    override fun draw(batch: Batch?, parentAlpha: Float) {
        validate()
        if (attachedPiece == null) {
            if (texture == null) {
                texture = Graphics.createPuzzlePieceTexture(
                    width.toInt(), height.toInt(), 5, notchRadius, Color.WHITE,
                    hasTopNotch = false, hasBottomBump = false, hasLeftBump = true
                )
            }
            texture?.let {
                batch?.draw(it, x - notchRadius, y, it.width.toFloat(), it.height.toFloat())
            }
        }
        super.draw(batch, parentAlpha)
    }

    override fun getPrefWidth(): Float {
        return attachedPiece?.width ?: 80f
    }

    override fun getPrefHeight(): Float {
        return attachedPiece?.height ?: 60f
    }

    override fun remove(): Boolean {
        texture?.dispose()
        texture = null
        return super.remove()
    }
}
