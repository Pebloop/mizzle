package com.pebloop.mizzle.player

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils

class GamePlayerScreen: Screen {
    override fun show() {
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.GREEN)
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
    }
}
