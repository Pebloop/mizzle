package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.data.components.data.DataComponent

class FloatDisplay: ComponentDisplay<Float> {
    override fun getId(): String {
        return "FLOAT"
    }

    override fun getDisplay(
        context: Context,
        data: Map.Entry<String, DataComponent<Float>>
    ): View {
        val linear = LinearLayout(context)
        val label = TextView(context)
        val input = TextInputEditText(context)

        label.text = data.key

        linear.orientation = LinearLayout.VERTICAL
        linear.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        input.layoutParams = params

        input.hint = "value"
        input.setText(data.value.data.toString())
        input.addTextChangedListener { text ->
            val value = text.toString().toFloatOrNull()
            if (value != null) {
                data.value.data = value
            }
        }
        linear.addView(label)
        linear.addView(input)

        return linear
    }

}
