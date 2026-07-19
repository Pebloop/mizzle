package com.pebloop.mizzle.android.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.Component
import com.pebloop.mizzle.data.ComponentData
import com.pebloop.mizzle.data.EntityData

class EditorEntityEditorActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    var entity: EntityData? = null
    var buttons: Array<Button> = arrayOf()

    val transformEditorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val newEntity = intent!!.getSerializableExtra("entity", EntityData::class.java)
            entity = newEntity
        }
    }

    val addComponentEditorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val newComponentData = intent!!.getSerializableExtra("component", ComponentData::class.java)!!
            val newComponentInstance = Component(newComponentData)
            entity!!.components = entity!!.components.plus(newComponentInstance)

            refreshComponentList()
        }
    }

    private fun refreshComponentList() {
        val componentsContainer: LinearLayout = findViewById(R.id.entity_components_container)
        componentsContainer.removeAllViews()
        buttons = arrayOf()

        for ((index, component) in entity!!.components.withIndex()) {
            val horizontal = LinearLayout(this)
            horizontal.orientation = LinearLayout.HORIZONTAL
            horizontal.gravity = Gravity.CENTER_VERTICAL

            val editButton = Button(this)
            editButton.text = component.name
            editButton.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            editButton.setOnClickListener {
                val editComponentIntent = Intent(this, EditorComponentEditorActivity::class.java)
                editComponentIntent.putExtra("index", index)
                editComponentIntent.putExtra("component", entity!!.components[index])
                editComponentEditorLauncher.launch(editComponentIntent)
            }
            horizontal.addView(editButton)
            buttons = buttons.plus(editButton)

            val deleteButton = ImageButton(this)
            deleteButton.setImageResource(R.drawable.back_button)
            deleteButton.background = null
            deleteButton.scaleX = 0.5f
            deleteButton.scaleY = 0.5f
            deleteButton.setOnClickListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Component")
                    .setMessage("Are you sure you want to delete ${component.name}?")
                    .setPositiveButton("Delete") { _, _ ->
                        entity!!.components = entity!!.components.filterIndexed { i, _ -> i != index }.toTypedArray()
                        refreshComponentList()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            horizontal.addView(deleteButton)

            componentsContainer.addView(horizontal)
        }
    }

    val editComponentEditorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val newComponent = intent!!.getSerializableExtra("component", Component::class.java)
            val index = intent.getIntExtra("index", -1)
            entity!!.components[index] = newComponent!!
            refreshComponentList()
        }
    }


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
        val transformButton: Button = findViewById(R.id.entity_editor_transform_button)
        val componentsContainer: LinearLayout = findViewById(R.id.entity_components_container)
        val addComponentButton: ImageButton = findViewById(R.id.entity_add_component)

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
        transformButton.setOnClickListener {
            val transformIntent: Intent = Intent(this, EditorEntityEditorTransformActivity::class.java)
            transformIntent.putExtra("entity", entity)
            transformEditorLauncher.launch(transformIntent)
        }

        refreshComponentList()

        addComponentButton.setOnClickListener {
            val addCOmponentIntent: Intent = Intent(this, EditorAddComponentActivity::class.java)
            addComponentEditorLauncher.launch(addCOmponentIntent)
        }

    }

    override fun exit() {
    }
}
