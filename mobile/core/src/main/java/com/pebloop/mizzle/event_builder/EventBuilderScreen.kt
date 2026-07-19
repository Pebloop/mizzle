package com.pebloop.mizzle.event_builder

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.pebloop.mizzle.data.components.Event
import com.pebloop.mizzle.editor.EditorActionsExtern

class EventBuilderScreen(val event: Event?, val actions: EditorActionsExtern?) : Screen {
    private val batch = SpriteBatch()
    private val font = BitmapFont()

    override fun show() {
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.DARK_GRAY)

        batch.begin()
        font.draw(batch, "Event Builder", 100f, Gdx.graphics.height - 100f)
        if (event != null) {
            font.draw(batch, "Editing event: ${event.code}", 100f, Gdx.graphics.height - 150f)
        }
        font.draw(batch, "Tap to exit (simulating save)", 100f, 100f)
        batch.end()

        if (Gdx.input.justTouched()) {
            actions?.exitEditor()
        }
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
