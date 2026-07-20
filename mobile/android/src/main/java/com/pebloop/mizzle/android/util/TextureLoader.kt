package com.pebloop.mizzle.android.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.data.DropletData

object TextureLoader {
    private const val TAG = "TextureLoader"

    fun loadDropletTextures(context: Context, droplet: DropletData, engine: Main?) {
        if (engine == null) return

        droplet.textures.forEach { resource ->
            if (resource.path.isNotEmpty()) {
                try {
                    val uri = Uri.parse(resource.path)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        val pixmap = Pixmap(bitmap.width, bitmap.height, Pixmap.Format.RGBA8888)
                        for (y in 0 until bitmap.height) {
                            for (x in 0 until bitmap.width) {
                                val color = bitmap.getPixel(x, y)
                                val r = (color shr 16) and 0xff
                                val g = (color shr 8) and 0xff
                                val b = color and 0xff
                                val a = (color shr 24) and 0xff
                                // Flip Y for LibGDX
                                pixmap.drawPixel(x, y, (r shl 24) or (g shl 16) or (b shl 8) or a)
                            }
                        }
                        engine.setUserTexture(resource.name, Texture(pixmap))
                        pixmap.dispose()
                        bitmap.recycle()
                        inputStream?.close()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load texture: ${resource.name}", e)
                }
            }
        }
    }
}
