package com.pebloop.mizzle.android.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.EntityData
import com.pebloop.mizzle.data.components.TimelineAnimationData

class EditorTimelineAnimationListActivity : AppCompatActivity() {

    private var animations: ArrayList<TimelineAnimationData> = ArrayList()
    private var entity: EntityData? = null
    private var editingIndex: Int = -1

    private val animationEditorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val returnedAnimation = result.data?.getSerializableExtra("animation", TimelineAnimationData::class.java)
            if (returnedAnimation != null) {
                if (editingIndex != -1) {
                    animations[editingIndex] = returnedAnimation
                } else {
                    animations.add(returnedAnimation)
                }
                refreshList()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timeline_animation_list)

        @Suppress("UNCHECKED_CAST")
        animations = savedInstanceState?.getSerializable("animations", ArrayList::class.java) as? ArrayList<TimelineAnimationData>
            ?: intent.getSerializableExtra("animations", ArrayList::class.java) as? ArrayList<TimelineAnimationData>
            ?: ArrayList()

        entity = savedInstanceState?.getSerializable("entity", EntityData::class.java)
            ?: intent.getSerializableExtra("entity", EntityData::class.java)

        editingIndex = savedInstanceState?.getInt("editingIndex", -1) ?: -1

        findViewById<ImageButton>(R.id.add_animation_button).setOnClickListener {
            editingIndex = -1
            val intent = Intent(this, EditorTimelineAnimationEditorActivity::class.java)
            intent.putExtra("entity", entity)
            animationEditorLauncher.launch(intent)
        }

        refreshList()
    }

    private fun refreshList() {
        val container = findViewById<LinearLayout>(R.id.animations_container)
        container.removeAllViews()

        animations.forEachIndexed { index, animation ->
            val button = Button(this)
            button.text = animation.name
            button.setOnClickListener {
                editingIndex = index
                val intent = Intent(this, EditorTimelineAnimationEditorActivity::class.java)
                intent.putExtra("animation", animation)
                intent.putExtra("entity", entity)
                animationEditorLauncher.launch(intent)
            }
            container.addView(button)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("animations", animations)
        outState.putSerializable("entity", entity)
        outState.putInt("editingIndex", editingIndex)
    }

    override fun finish() {
        val resultIntent = Intent()
        resultIntent.putExtra("animations", animations)
        setResult(RESULT_OK, resultIntent)
        super.finish()
    }
}
