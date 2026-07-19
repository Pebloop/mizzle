package com.pebloop.mizzle.android.activities

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.EntityData


class EditorEntityEditorTransformActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    var entity: EntityData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transform)

        entity = intent.getSerializableExtra("entity", EntityData::class.java)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        val closeButton: ImageButton = findViewById(R.id.transform_close_button)
        val posXInput: TextInputEditText = findViewById(R.id.transform_pos_x_input)
        val posYInput: TextInputEditText = findViewById(R.id.transform_pos_y_input)
        val rotationInput: TextInputEditText = findViewById(R.id.transform_rotation_input)
        val scaleXInput: TextInputEditText = findViewById(R.id.transform_scale_x_input)
        val scaleYInput: TextInputEditText = findViewById(R.id.transform_scale_y_input)
        val zIndexInput: TextInputEditText = findViewById(R.id.transform_z_index_input)

        closeButton.setOnClickListener {
            intent.putExtra("entity", entity!!)
            setResult(RESULT_OK, intent)
            finish()
        }
        posXInput.setText(entity!!.transform.position.x.toString())
        posYInput.setText(entity!!.transform.position.y.toString())
        rotationInput.setText(entity!!.transform.rotation.toString())
        scaleXInput.setText(entity!!.transform.scale.x.toString())
        scaleYInput.setText(entity!!.transform.scale.y.toString())
        zIndexInput.setText(entity!!.transform.zIndex.toString())

        posXInput.addTextChangedListener { newText: Editable? ->
            val value = newText.toString().toFloatOrNull()
            if (value != null)
                entity!!.transform.position.x = value
        }
        posYInput.addTextChangedListener { newText: Editable? ->
            val value = newText.toString().toFloatOrNull()
            if (value != null)
                entity!!.transform.position.y = value
        }
        rotationInput.addTextChangedListener { newText: Editable? ->
            val value = newText.toString().toFloatOrNull()
            if (value != null)
                entity!!.transform.rotation = value
        }
        scaleXInput.addTextChangedListener { newText: Editable? ->
            val value = newText.toString().toFloatOrNull()
            if (value != null)
                entity!!.transform.scale.x = value
        }
        scaleYInput.addTextChangedListener { newText: Editable? ->
            val value = newText.toString().toFloatOrNull()
            if (value != null)
                entity!!.transform.scale.y = value
        }
        zIndexInput.addTextChangedListener { newText: Editable? ->
            val value = newText.toString().toIntOrNull()
            if (value != null)
                entity!!.transform.zIndex = value
        }
    }

    override fun exit() {

    }
}
