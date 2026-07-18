package com.pebloop.mizzle.android.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageButton
import android.widget.RemoteViews
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.DropletData
import com.pebloop.mizzle.data.EntityData

class EditorEntityEditorActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    var entity: EntityData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        entity = intent.getSerializableExtra("entity", EntityData::class.java)

        setContentView(R.layout.activity_entity_editor)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        val idText: TextView = findViewById(R.id.entity_id_text)
        val nameTextInput: TextInputEditText = findViewById(R.id.entity_name_input)
        val closeButton: ImageButton = findViewById(R.id.entity_editor_close)

        idText.text = "id: " + entity!!.id.toString()
        nameTextInput.setText(entity!!.name)
        nameTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                entity!!.name = p0.toString()

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        closeButton.setOnClickListener {
            intent.putExtra("entity", entity)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun exit() {
    }
}
