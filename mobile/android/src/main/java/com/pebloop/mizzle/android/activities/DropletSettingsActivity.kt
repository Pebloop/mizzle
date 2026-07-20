package com.pebloop.mizzle.android.activities

import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.materialswitch.MaterialSwitch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.DropletData

class DropletSettingsActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    private var droplet: DropletData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        droplet = intent.getSerializableExtra("droplet", DropletData::class.java)

        setContentView(R.layout.activity_droplet_settings)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        val nameInput: TextInputEditText = findViewById(R.id.droplet_name_input)
        val publicSwitch: MaterialSwitch = findViewById(R.id.droplet_is_draft)
        val closeButton: ImageButton = findViewById(R.id.droplet_settings_close)

        droplet?.let {
            nameInput.setText(it.name)
            publicSwitch.isChecked = !it.isPublic
        }

        closeButton.setOnClickListener {
            droplet?.let {
                it.name = nameInput.text.toString()
                it.isPublic = !publicSwitch.isChecked
                intent.putExtra("droplet", it)
                setResult(RESULT_OK, intent)
            }
            finish()
        }
    }

    override fun exit() {
    }
}
