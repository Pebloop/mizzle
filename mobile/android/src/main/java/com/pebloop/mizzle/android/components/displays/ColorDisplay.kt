package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.data.components.data.DataComponent
import androidx.core.graphics.toColorInt

class ColorDisplay : ComponentDisplay<Int> {
    override fun getId(): String {
        return "COLOR"
    }

    override fun getDisplay(
        context: Context,
        data: Map.Entry<String, DataComponent<Int>>
    ): View {
        val linear = LinearLayout(context)
        linear.orientation = LinearLayout.VERTICAL
        linear.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val label = TextView(context)
        label.text = data.key
        linear.addView(label)

        val horizontal = LinearLayout(context)
        horizontal.orientation = LinearLayout.HORIZONTAL
        horizontal.gravity = Gravity.CENTER_VERTICAL
        horizontal.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val colorPreview = View(context)
        val previewSize = (32 * context.resources.displayMetrics.density).toInt()
        val previewParams = LinearLayout.LayoutParams(previewSize, previewSize)
        previewParams.setMargins(0, 0, 16, 0)
        colorPreview.layoutParams = previewParams
        colorPreview.setBackgroundColor(data.value.data)

        colorPreview.isClickable = true
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        colorPreview.foreground = context.getDrawable(outValue.resourceId)

        horizontal.addView(colorPreview)

        val input = TextInputEditText(context)
        input.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        input.hint = "Hex Color (#AARRGGBB)"
        input.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS

        val hex = String.format("#%08X", data.value.data)
        input.setText(hex)

        colorPreview.setOnClickListener {
            showColorPickerDialog(context, data.value.data) { newColor ->
                val newHex = String.format("#%08X", newColor)
                input.setText(newHex)
            }
        }

        input.addTextChangedListener { text ->
            val colorStr = text.toString()
            try {
                if (colorStr.startsWith("#") && (colorStr.length == 7 || colorStr.length == 9)) {
                    val color = colorStr.toColorInt()
                    data.value.data = color
                    colorPreview.setBackgroundColor(color)
                }
            } catch (e: Exception) {
                // Ignore invalid hex
            }
        }

        horizontal.addView(input)
        linear.addView(horizontal)

        return linear
    }

    private fun showColorPickerDialog(context: Context, initialColor: Int, onColorSelected: (Int) -> Unit) {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * context.resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }

        val preview = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (64 * context.resources.displayMetrics.density).toInt()
            ).apply {
                setMargins(0, 0, 0, (16 * context.resources.displayMetrics.density).toInt())
            }
            setBackgroundColor(initialColor)
        }
        root.addView(preview)

        var currentColor = initialColor

        fun updatePreview() {
            preview.setBackgroundColor(currentColor)
        }

        fun createSlider(label: String, initialValue: Int, onValueChange: (Int) -> Unit) {
            val text = TextView(context).apply {
                text = "$label: $initialValue"
                setPadding(0, (8 * context.resources.displayMetrics.density).toInt(), 0, 0)
            }
            val seekBar = SeekBar(context).apply {
                max = 255
                progress = initialValue
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                        text.text = "$label: $p"
                        onValueChange(p)
                        updatePreview()
                    }

                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
            }
            root.addView(text)
            root.addView(seekBar)
        }

        createSlider("Alpha", Color.alpha(initialColor)) {
            currentColor = Color.argb(it, Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor))
        }
        createSlider("Red", Color.red(initialColor)) {
            currentColor = Color.argb(Color.alpha(currentColor), it, Color.green(currentColor), Color.blue(currentColor))
        }
        createSlider("Green", Color.green(initialColor)) {
            currentColor = Color.argb(Color.alpha(currentColor), Color.red(currentColor), it, Color.blue(currentColor))
        }
        createSlider("Blue", Color.blue(initialColor)) {
            currentColor = Color.argb(Color.alpha(currentColor), Color.red(currentColor), Color.green(currentColor), it)
        }

        MaterialAlertDialogBuilder(context)
            .setTitle("Pick Color")
            .setView(root)
            .setPositiveButton("OK") { _, _ -> onColorSelected(currentColor) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
