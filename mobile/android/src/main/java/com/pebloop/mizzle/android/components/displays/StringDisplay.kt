package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.pebloop.mizzle.data.components.data.DataComponent

class StringDisplay: ComponentDisplay<String> {
    override fun getId(): String {
        return "STRING"
    }

    override fun getDisplay(
        context: Context,
        data: Map.Entry<String, DataComponent<String>>
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
        input.setText(data.value.data)
        input.addTextChangedListener { text ->
            data.value.data = text.toString()
        }
        linear.addView(label)
        linear.addView(input)

        return linear
    }

}
