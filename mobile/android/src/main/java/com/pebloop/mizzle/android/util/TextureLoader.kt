package com.pebloop.mizzle.android.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.data.DropletData

object TextureLoader {
    private const val TAG = "TextureLoader"

    fun loadDropletTextures(context: Context, droplet: DropletData, engine: Main?, clearCache: Boolean = false) {
        if (engine == null) return

        if (clearCache) {
            engine.clearCache()
        }

        droplet.textures.forEach { resource ->
            if (resource.path.isNotEmpty()) {
                try {
                    val uri = Uri.parse(resource.path)

                    Gdx.app.postRunnable {
                        try {
                            var texture = engine.getCachedTexture(resource.path)

                            if (texture == null) {
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                if (bitmap != null) {
                                    Log.d(TAG, "Decoding bitmap for ${resource.name}: ${bitmap.width}x${bitmap.height}")
                                    val pixmap = Pixmap(bitmap.width, bitmap.height, Pixmap.Format.RGBA8888)
                                    val pixels = IntArray(bitmap.width * bitmap.height)
                                    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

                                    for (y in 0 until bitmap.height) {
                                        for (x in 0 until bitmap.width) {
                                            val color = pixels[y * bitmap.width + x]
                                            val r = (color shr 16) and 0xff
                                            val g = (color shr 8) and 0xff
                                            val b = color and 0xff
                                            val a = (color shr 24) and 0xff
                                            pixmap.drawPixel(x, y, (r shl 24) or (g shl 16) or (b shl 8) or a)
                                        }
                                    }
                                    texture = Texture(pixmap)
                                    engine.addCachedTexture(resource.path, texture)
                                    pixmap.dispose()
                                    bitmap.recycle()
                                    inputStream?.close()
                                } else {
                                    Log.e(TAG, "Failed to decode bitmap for ${resource.name}")
                                }
                            }

                            if (texture != null) {
                                val x = resource.regionX
                                val y = resource.regionY
                                val w = if (resource.regionWidth == -1) texture.width - x else resource.regionWidth
                                val h = if (resource.regionHeight == -1) texture.height - y else resource.regionHeight

                                val region = TextureRegion(texture, x, y, w, h)
                                engine.setUserTexture(resource.name, region)
                                Log.d(TAG, "Loaded texture region: ${resource.name} (${x},${y},${w},${h})")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error creating texture on GL thread: ${resource.name}", e)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to prepare texture: ${resource.name}", e)
                }
            }
        }
    }
}
