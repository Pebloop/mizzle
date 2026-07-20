package com.pebloop.mizzle.android.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.ResourceData
import com.pebloop.mizzle.data.components.SpriteAnimationData

class EditorAnimationEditorActivity : AppCompatActivity() {

    private var animation: SpriteAnimationData = SpriteAnimationData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_animation_editor)

        animation = savedInstanceState?.getSerializable("animation", SpriteAnimationData::class.java)
            ?: intent.getSerializableExtra("animation", SpriteAnimationData::class.java)
            ?: SpriteAnimationData()

        val nameInput = findViewById<TextInputEditText>(R.id.name_input)
        val durationInput = findViewById<TextInputEditText>(R.id.duration_input)
        val loopingSwitch = findViewById<MaterialSwitch>(R.id.looping_switch)

        nameInput.setText(animation.name)
        durationInput.setText(animation.frameDuration.toString())
        loopingSwitch.isChecked = animation.looping

        findViewById<ImageButton>(R.id.add_frame_button).setOnClickListener {
            animation.textures = animation.textures.plus("")
            refreshFrames()
        }

        findViewById<Button>(R.id.save_button).setOnClickListener {
            animation.name = nameInput.text.toString()
            animation.frameDuration = durationInput.text.toString().toFloatOrNull() ?: 0.1f
            animation.looping = loopingSwitch.isChecked

            val resultIntent = Intent()
            resultIntent.putExtra("animation", animation)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        refreshFrames()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("animation", animation)
    }

    private fun refreshFrames() {
        val container = findViewById<LinearLayout>(R.id.frames_container)
        container.removeAllViews()

        val textures = Main.getInstance().droplet.textures
        val spinnerItems = arrayOf(ResourceData("None", "")) + textures

        animation.textures.forEachIndexed { index, currentTextureName ->
            val frameView = LayoutInflater.from(this).inflate(R.layout.item_spinner_texture, container, false)
            val preview = frameView.findViewById<ImageView>(R.id.spinner_texture_preview)
            val spinner = Spinner(this) // We'll add a spinner to the layout or just use a spinner directly

            // For simplicity in this UI, let's just replace the TextView with a Spinner or add one
            val linear = frameView as LinearLayout
            val nameText = frameView.findViewById<TextView>(R.id.spinner_texture_name)
            linear.removeView(nameText)

            val spinnerParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            spinner.layoutParams = spinnerParams
            linear.addView(spinner)

            val deleteBtn = ImageButton(this)
            deleteBtn.setImageResource(R.drawable.delete_button)
            deleteBtn.background = null
            deleteBtn.setOnClickListener {
                animation.textures = animation.textures.filterIndexed { i, _ -> i != index }
                refreshFrames()
            }
            linear.addView(deleteBtn)

            val adapter = object : ArrayAdapter<ResourceData>(this, R.layout.item_spinner_texture, spinnerItems) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return createViewFromResource(position, convertView, parent)
                }
                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return createViewFromResource(position, convertView, parent)
                }
                private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_spinner_texture, parent, false)
                    val p: ImageView = view.findViewById(R.id.spinner_texture_preview)
                    val n: TextView = view.findViewById(R.id.spinner_texture_name)
                    val item = getItem(position)!!
                    n.text = item.name
                    if (item.path.isNotEmpty()) {
                        try {
                            val uri = Uri.parse(item.path)
                            val inputStream = contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            p.setImageBitmap(bitmap)
                        } catch (e: Exception) { p.setImageResource(R.drawable.icon_mizzle) }
                    } else { p.setImageResource(R.drawable.icon_mizzle) }
                    return view
                }
            }
            spinner.adapter = adapter
            val selectedIdx = spinnerItems.indexOfFirst { it.name == currentTextureName }
            if (selectedIdx != -1) spinner.setSelection(selectedIdx)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = spinnerItems[position]
                    val newTextures = animation.textures.toMutableList()
                    newTextures[index] = if (selected.name == "None") "" else selected.name
                    animation.textures = newTextures

                    // Update preview
                    if (selected.path.isNotEmpty()) {
                        try {
                            val uri = Uri.parse(selected.path)
                            val inputStream = contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            preview.setImageBitmap(bitmap)
                        } catch (e: Exception) { preview.setImageResource(R.drawable.icon_mizzle) }
                    } else { preview.setImageResource(R.drawable.icon_mizzle) }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            container.addView(frameView)
        }
    }
}
