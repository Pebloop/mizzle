package com.pebloop.mizzle.android.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.badlogic.gdx.math.Vector2
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.EntityData
import com.pebloop.mizzle.data.components.TimelineAnimationData
import com.pebloop.mizzle.data.components.TimelineKeyframeData
import com.pebloop.mizzle.data.components.TimelineTrackData

class EditorTimelineAnimationEditorActivity : AppCompatActivity() {

    private var animation: TimelineAnimationData = TimelineAnimationData()
    private var entity: EntityData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timeline_animation_editor)

        animation = savedInstanceState?.getSerializable("animation", TimelineAnimationData::class.java)
            ?: intent.getSerializableExtra("animation", TimelineAnimationData::class.java)
            ?: TimelineAnimationData()

        entity = savedInstanceState?.getSerializable("entity", EntityData::class.java)
            ?: intent.getSerializableExtra("entity", EntityData::class.java)

        val nameInput = findViewById<TextInputEditText>(R.id.name_input)
        val durationInput = findViewById<TextInputEditText>(R.id.duration_input)
        val loopingSwitch = findViewById<MaterialSwitch>(R.id.looping_switch)

        nameInput.setText(animation.name)
        durationInput.setText(animation.duration.toString())
        loopingSwitch.isChecked = animation.looping

        findViewById<ImageButton>(R.id.add_track_button).setOnClickListener {
            animation.tracks.add(TimelineTrackData())
            refreshTracks()
        }

        findViewById<Button>(R.id.save_button).setOnClickListener {
            animation.name = nameInput.text.toString()
            animation.duration = durationInput.text.toString().toFloatOrNull() ?: 1.0f
            animation.looping = loopingSwitch.isChecked

            val resultIntent = Intent()
            resultIntent.putExtra("animation", animation)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        refreshTracks()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("animation", animation)
        outState.putSerializable("entity", entity)
    }

    private fun refreshTracks() {
        val container = findViewById<LinearLayout>(R.id.tracks_container)
        container.removeAllViews()

        animation.tracks.forEachIndexed { index, track ->
            val trackView = LayoutInflater.from(this).inflate(R.layout.item_timeline_track, container, false)
            val componentSpinner = trackView.findViewById<Spinner>(R.id.component_spinner)
            val propertySpinner = trackView.findViewById<Spinner>(R.id.property_spinner)
            val deleteTrackBtn = trackView.findViewById<ImageButton>(R.id.delete_track_button)
            val addKeyframeBtn = trackView.findViewById<ImageButton>(R.id.add_keyframe_button)
            val keyframesContainer = trackView.findViewById<LinearLayout>(R.id.keyframes_container)

            // Setup component spinner
            val components = entity?.components?.map { it.name } ?: listOf("None")
            val compAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, components)
            compAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            componentSpinner.adapter = compAdapter
            componentSpinner.setSelection(track.componentIndex.coerceIn(0, components.size - 1))

            componentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    track.componentIndex = pos
                    setupPropertySpinner(propertySpinner, track)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            setupPropertySpinner(propertySpinner, track)

            deleteTrackBtn.setOnClickListener {
                animation.tracks.removeAt(index)
                refreshTracks()
            }

            addKeyframeBtn.setOnClickListener {
                track.keyframes.add(TimelineKeyframeData())
                refreshKeyframes(keyframesContainer, track)
            }

            refreshKeyframes(keyframesContainer, track)
            container.addView(trackView)
        }
    }

    private fun setupPropertySpinner(spinner: Spinner, track: TimelineTrackData) {
        val targetComponent = entity?.components?.getOrNull(track.componentIndex)
        val properties = targetComponent?.datas?.keys?.toList() ?: listOf()
        val propAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, properties)
        propAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = propAdapter

        val selectedIdx = properties.indexOf(track.propertyName)
        if (selectedIdx != -1) spinner.setSelection(selectedIdx)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                track.propertyName = properties[pos]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun refreshKeyframes(container: LinearLayout, track: TimelineTrackData) {
        container.removeAllViews()
        track.keyframes.forEachIndexed { index, keyframe ->
            val keyframeView = LayoutInflater.from(this).inflate(R.layout.item_timeline_keyframe, container, false)
            val timeEdit = keyframeView.findViewById<EditText>(R.id.keyframe_time)
            val valueEdit = keyframeView.findViewById<EditText>(R.id.keyframe_value)
            val deleteBtn = keyframeView.findViewById<ImageButton>(R.id.delete_keyframe_button)

            timeEdit.setText(keyframe.time.toString())
            valueEdit.setText(valueToString(keyframe.value))

            timeEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    keyframe.time = s.toString().toFloatOrNull() ?: 0f
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            valueEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    keyframe.value = stringToValue(s.toString())
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            deleteBtn.setOnClickListener {
                track.keyframes.removeAt(index)
                refreshKeyframes(container, track)
            }

            container.addView(keyframeView)
        }
    }

    private fun valueToString(value: Any?): String {
        return when (value) {
            is Vector2 -> "${value.x},${value.y}"
            null -> ""
            else -> value.toString()
        }
    }

    private fun stringToValue(s: String): Any? {
        if (s.contains(",")) {
            val parts = s.split(",")
            if (parts.size == 2) {
                val x = parts[0].toFloatOrNull() ?: 0f
                val y = parts[1].toFloatOrNull() ?: 0f
                return Vector2(x, y)
            }
        }
        return s.toFloatOrNull() ?: s
    }
}
