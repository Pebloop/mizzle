package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.data.components.data.DataComponent
import com.pebloop.mizzle.data.util.SerializableVector2

class Vector2Display: ComponentDisplay<SerializableVector2> {
    override fun getId(): String {
        return "VECTOR2"
    }

    override fun getDisplay(context: Context, data: Map.Entry<String, DataComponent<SerializableVector2>>): View {
        val linear = LinearLayout(context)
        val label = TextView(context)
        val horizontal = LinearLayout(context)
        val inputX = TextInputEditText(context)
        val inputY = TextInputEditText(context)

        val vector = data.value.data as? SerializableVector2 ?: SerializableVector2(0f, 0f)

        label.text = data.key
        linear.orientation = LinearLayout.VERTICAL
        linear.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        horizontal.orientation = LinearLayout.HORIZONTAL
        horizontal.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        inputX.layoutParams = params
        inputY.layoutParams = params

        inputX.hint = "x"
        inputY.hint = "y"
        inputX.setText(vector.x.toString())
        inputY.setText(vector.y.toString())
        inputX.addTextChangedListener { text ->
            val x = text.toString().toFloatOrNull()
            if (x != null) {
                (data.value.data as? SerializableVector2)?.x = x
            }
        }
        inputY.addTextChangedListener { text ->
            val y = text.toString().toFloatOrNull()
            if (y != null) {
                (data.value.data as? SerializableVector2)?.y = y
            }
        }

        horizontal.addView(inputX)
        horizontal.addView(inputY)
        linear.addView(label)
        linear.addView(horizontal)

        return linear
    }
}
