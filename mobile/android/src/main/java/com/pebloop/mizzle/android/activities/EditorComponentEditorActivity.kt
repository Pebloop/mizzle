package com.pebloop.mizzle.android.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.R
import com.pebloop.mizzle.android.components.displays.ComponentDisplay
import com.pebloop.mizzle.android.components.displays.ComponentDisplays
import com.pebloop.mizzle.data.Component
import com.pebloop.mizzle.data.components.Event
import com.pebloop.mizzle.data.components.data.DataComponent


class EditorComponentEditorActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    private var component: Component? = null
    private var index: Int = -1
    private var currentEventKey: String? = null

    private val eventBuilderLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val returnedEvent = result.data?.getSerializableExtra("event", Event::class.java)
            if (returnedEvent != null && currentEventKey != null) {
                @Suppress("UNCHECKED_CAST")
                (component!!.datas[currentEventKey!!] as? DataComponent<Event>)?.data = returnedEvent
                refreshDisplays()
            }
        }
    }

    fun launchEventBuilder(key: String, event: Event) {
        currentEventKey = key
        val intent = Intent(this, EventBuilderActivity::class.java)
        intent.putExtra("event", event)
        eventBuilderLauncher.launch(intent)
    }

    private fun refreshDisplays() {
        val editorContainer: LinearLayout = findViewById(R.id.component_editor_container)
        editorContainer.removeAllViews()

        for (data in component!!.datas) {
            val display = ComponentDisplays.getById(data.value.displayId)
            if (display != null) {
                @Suppress("UNCHECKED_CAST")
                editorContainer.addView((display as ComponentDisplay<Any>).getDisplay(this, data as Map.Entry<String, DataComponent<Any>>))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_component_editor)

        component = intent.getSerializableExtra("component", Component::class.java)!!
        index = intent.getIntExtra("index", -1)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        val closeButton: ImageButton = findViewById(R.id.component_editor_close_button)
        val nameInput: TextInputEditText = findViewById(R.id.component_editor_name)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveAndExit(component!!, index)
            }
        })

        closeButton.setOnClickListener {
            saveAndExit(component!!, index)
        }

        nameInput.setText(component!!.name)
        nameInput.addTextChangedListener { text: Editable? ->
            component!!.name = text.toString()
        }

        refreshDisplays()
    }

    private fun saveAndExit(component: Component, index: Int) {
        val resultIntent = Intent()
        resultIntent.putExtra("component", component)
        resultIntent.putExtra("index", index)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun exit() {

    }
}
