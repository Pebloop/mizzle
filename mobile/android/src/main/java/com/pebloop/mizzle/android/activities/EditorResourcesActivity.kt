package com.pebloop.mizzle.android.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.DropletData

class EditorResourcesActivity : AppCompatActivity() {

    var droplet: DropletData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resources)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.textures_title).parent as LinearLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 16, systemBars.top + 16, systemBars.right + 16, systemBars.bottom + 16)
            insets
        }

        droplet = intent.getSerializableExtra("droplet", DropletData::class.java)

        val addTextureButton: ImageButton = findViewById(R.id.add_texture_button)
        val addAudioButton: ImageButton = findViewById(R.id.add_audio_button)

        refreshLists()

        addTextureButton.setOnClickListener {
            // Stub: Add a dummy texture for now
            droplet?.let {
                it.textures = it.textures.plus("texture_${it.textures.size + 1}")
                refreshLists()
            }
        }

        addAudioButton.setOnClickListener {
            // Stub: Add a dummy audio for now
            droplet?.let {
                it.audios = it.audios.plus("audio_${it.audios.size + 1}")
                refreshLists()
            }
        }
    }

    private fun refreshLists() {
        val texturesList: LinearLayout = findViewById(R.id.textures_list)
        val audiosList: LinearLayout = findViewById(R.id.audios_list)

        texturesList.removeAllViews()
        audiosList.removeAllViews()

        droplet?.textures?.forEach { texture ->
            val tv = TextView(this)
            tv.text = texture
            texturesList.addView(tv)
        }

        droplet?.audios?.forEach { audio ->
            val tv = TextView(this)
            tv.text = audio
            audiosList.addView(tv)
        }
    }

    override fun finish() {
        intent.putExtra("droplet", droplet)
        setResult(RESULT_OK, intent)
        super.finish()
    }
}
