package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.badlogic.gdx.math.Vector2
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.data.components.data.DataComponent

class Vector2Display: ComponentDisplay<Vector2> {
    override fun getId(): String {
        return "VECTOR2"
    }

    override fun getDisplay(context: Context, data: Map.Entry<String, DataComponent<Vector2>>): View {
        val linear = LinearLayout(context)
        val label = TextView(context)
        val horizontal = LinearLayout(context)
        val inputX = TextInputEditText(context)
        val inputY = TextInputEditText(context)

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
        inputX.setText(data.value.data.x.toString())
        inputY.setText(data.value.data.y.toString())
        inputX.addTextChangedListener { text ->
            val x = text.toString().toFloatOrNull()
            if (x != null) {
                data.value.data.x = x
            }
        }
        inputY.addTextChangedListener { text ->
            val y = text.toString().toFloatOrNull()
            if (y != null) {
                data.value.data.y = y
            }
        }

        horizontal.addView(inputX)
        horizontal.addView(inputY)
        linear.addView(label)
        linear.addView(horizontal)

        return linear
    }
}
