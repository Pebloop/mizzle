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
import com.pebloop.mizzle.data.components.SpriteAnimationData
import com.pebloop.mizzle.data.components.TimelineAnimationData
import com.pebloop.mizzle.data.components.Event
import com.pebloop.mizzle.data.components.data.DataComponent
import com.pebloop.mizzle.data.EntityData


class EditorComponentEditorActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    private var component: Component? = null
    private var entity: EntityData? = null
    private var index: Int = -1
    private var currentEventKey: String? = null
    private var currentAnimationsKey: String? = null
    private var currentTimelineAnimationsKey: String? = null

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

    private val animationsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            @Suppress("UNCHECKED_CAST")
            val returnedAnimations = result.data?.getSerializableExtra("animations", ArrayList::class.java) as? ArrayList<SpriteAnimationData>
            if (returnedAnimations != null && currentAnimationsKey != null) {
                @Suppress("UNCHECKED_CAST")
                (component!!.datas[currentAnimationsKey!!] as? DataComponent<ArrayList<SpriteAnimationData>>)?.data = returnedAnimations
                refreshDisplays()
            }
        }
    }

    private val timelineAnimationsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            @Suppress("UNCHECKED_CAST")
            val returnedAnimations = result.data?.getSerializableExtra("animations", ArrayList::class.java) as? ArrayList<TimelineAnimationData>
            if (returnedAnimations != null && currentTimelineAnimationsKey != null) {
                @Suppress("UNCHECKED_CAST")
                (component!!.datas[currentTimelineAnimationsKey!!] as? DataComponent<ArrayList<TimelineAnimationData>>)?.data = returnedAnimations
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

    fun launchAnimationListEditor(key: String, animations: ArrayList<SpriteAnimationData>) {
        currentAnimationsKey = key
        val intent = Intent(this, EditorAnimationListActivity::class.java)
        intent.putExtra("animations", animations)
        animationsLauncher.launch(intent)
    }

    fun launchTimelineAnimationEditor(key: String, animations: ArrayList<TimelineAnimationData>) {
        currentTimelineAnimationsKey = key
        val intent = Intent(this, EditorTimelineAnimationListActivity::class.java)
        intent.putExtra("animations", animations)
        intent.putExtra("entity", entity)
        timelineAnimationsLauncher.launch(intent)
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

        component = savedInstanceState?.getSerializable("component", Component::class.java)
            ?: intent.getSerializableExtra("component", Component::class.java)
        entity = savedInstanceState?.getSerializable("entity", EntityData::class.java)
            ?: intent.getSerializableExtra("entity", EntityData::class.java)
        index = savedInstanceState?.getInt("index", -1) ?: intent.getIntExtra("index", -1)
        currentEventKey = savedInstanceState?.getString("currentEventKey")
        currentAnimationsKey = savedInstanceState?.getString("currentAnimationsKey")
        currentTimelineAnimationsKey = savedInstanceState?.getString("currentTimelineAnimationsKey")

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("component", component)
        outState.putSerializable("entity", entity)
        outState.putInt("index", index)
        outState.putString("currentEventKey", currentEventKey)
        outState.putString("currentAnimationsKey", currentAnimationsKey)
        outState.putString("currentTimelineAnimationsKey", currentTimelineAnimationsKey)
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
