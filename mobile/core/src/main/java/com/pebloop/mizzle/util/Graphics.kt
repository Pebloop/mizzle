package com.pebloop.mizzle.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture

object Graphics {
    fun createWhitePixelTexture(): Texture {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
    }

    fun createCircleTexture(): Texture {
        val size = 256
        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fillCircle(size / 2, size / 2, size / 2 - 1)
        val texture = Texture(pixmap)
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        pixmap.dispose()
        return texture
    }

    fun createPuzzlePieceTexture(
        width: Int,
        height: Int,
        radius: Int,
        notchRadius: Int,
        color: Color,
        hasTopNotch: Boolean,
        hasBottomBump: Boolean,
        hasLeftBump: Boolean,
        hasLeftNotch: Boolean = false
    ): Texture {
        val extraWidthLeft = if (hasLeftBump || hasLeftNotch) notchRadius else 0
        val extraHeightTop = if (hasTopNotch) notchRadius else 0
        val extraHeightBottom = if (hasBottomBump) notchRadius else 0

        val pixmap = Pixmap(width + extraWidthLeft, height + extraHeightTop + extraHeightBottom, Pixmap.Format.RGBA8888)

        pixmap.setColor(color)
        val bodyX = extraWidthLeft
        val bodyY = extraHeightTop

        // 1. Draw rounded rectangle body
        pixmap.fillRectangle(bodyX + radius, bodyY, width - radius * 2, height)
        pixmap.fillRectangle(bodyX, bodyY + radius, width, height - radius * 2)
        // Corners
        pixmap.fillCircle(bodyX + radius, bodyY + radius, radius)
        pixmap.fillCircle(bodyX + width - radius, bodyY + radius, radius)
        pixmap.fillCircle(bodyX + radius, bodyY + height - radius, radius)
        pixmap.fillCircle(bodyX + width - radius, bodyY + height - radius, radius)

        val notchOffset = radius + notchRadius + 20

        // 2. Draw "Bump" at the bottom
        if (hasBottomBump) {
            pixmap.fillCircle(bodyX + notchOffset, bodyY + height, notchRadius)
        }

        // 3. Draw "Notch" at the top
        if (hasTopNotch) {
            pixmap.blending = Pixmap.Blending.None
            pixmap.setColor(0f, 0f, 0f, 0f)
            pixmap.fillCircle(bodyX + notchOffset, bodyY, notchRadius)
            pixmap.blending = Pixmap.Blending.SourceOver
        }

        // 4. Draw "Bump" at the left
        if (hasLeftBump) {
            pixmap.setColor(color)
            pixmap.fillCircle(bodyX, bodyY + height / 2, notchRadius)
        }

        // 5. Draw "Notch" at the left
        if (hasLeftNotch) {
            pixmap.blending = Pixmap.Blending.None
            pixmap.setColor(0f, 0f, 0f, 0f)
            pixmap.fillCircle(bodyX, bodyY + height / 2, notchRadius)
            pixmap.blending = Pixmap.Blending.SourceOver
        }

        val texture = Texture(pixmap)
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        pixmap.dispose()
        return texture
    }
}
