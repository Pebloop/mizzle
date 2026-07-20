package com.pebloop.mizzle.android.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.ResourceData

class EditorTilesetEditorActivity : AppCompatActivity() {

    private var baseResource: ResourceData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tileset_editor)

        baseResource = intent.getSerializableExtra("resource", ResourceData::class.java)

        val preview: ImageView = findViewById(R.id.tileset_preview)
        val widthInput: TextInputEditText = findViewById(R.id.tile_width_input)
        val heightInput: TextInputEditText = findViewById(R.id.tile_height_input)
        val prefixInput: TextInputEditText = findViewById(R.id.prefix_input)
        val splitButton: Button = findViewById(R.id.split_button)

        baseResource?.let { res ->
            try {
                val uri = Uri.parse(res.path)
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                preview.setImageBitmap(bitmap)
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        splitButton.setOnClickListener {
            val tileW = widthInput.text.toString().toIntOrNull() ?: 32
            val tileH = heightInput.text.toString().toIntOrNull() ?: 32
            val prefix = prefixInput.text.toString()

            val generatedResources = ArrayList<ResourceData>()

            baseResource?.let { res ->
                try {
                    val uri = Uri.parse(res.path)
                    val inputStream = contentResolver.openInputStream(uri)
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(inputStream, null, options)
                    inputStream?.close()

                    val fullW = options.outWidth
                    val fullH = options.outHeight

                    val cols = fullW / tileW
                    val rows = fullH / tileH

                    var count = 0
                    for (row in 0 until rows) {
                        for (col in 0 until cols) {
                            val name = "${prefix}${count++}"
                            val x = col * tileW
                            val y = row * tileH
                            generatedResources.add(ResourceData(name, res.path, x, y, tileW, tileH))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val resultIntent = Intent()
            resultIntent.putExtra("resources", generatedResources)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
