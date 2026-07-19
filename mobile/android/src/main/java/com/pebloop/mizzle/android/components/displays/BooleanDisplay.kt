package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import com.pebloop.mizzle.data.components.data.DataComponent

class BooleanDisplay : ComponentDisplay<Boolean> {
    override fun getId(): String {
        return "BOOLEAN"
    }

    override fun getDisplay(
        context: Context,
        data: Map.Entry<String, DataComponent<Boolean>>
    ): View {
        val linear = LinearLayout(context)
        val switch = Switch(context)

        linear.orientation = LinearLayout.VERTICAL
        linear.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        switch.text = data.key
        switch.isChecked = data.value.data
        switch.setOnCheckedChangeListener { _, isChecked ->
            data.value.data = isChecked
        }

        linear.addView(switch)

        return linear
    }
}
