package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.pebloop.mizzle.android.activities.EventBuilderActivity
import com.pebloop.mizzle.data.components.Event
import com.pebloop.mizzle.data.components.data.DataComponent

class EventDisplay : ComponentDisplay<Event> {
    override fun getId(): String = "EVENT"

    override fun getDisplay(
        context: Context,
        data: Map.Entry<String, DataComponent<Event>>
    ): View {
        val linear = LinearLayout(context)
        val label = TextView(context)
        val preview = TextView(context)
        val button = Button(context)

        label.text = data.key
        preview.text = "Code: ${data.value.data.code}"

        linear.orientation = LinearLayout.VERTICAL
        linear.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        button.text = "Open Event Builder"
        button.setOnClickListener {
            val intent = Intent(context, EventBuilderActivity::class.java)
            intent.putExtra("event", data.value.data)
            context.startActivity(intent)
        }

        linear.addView(label)
        linear.addView(preview)
        linear.addView(button)

        return linear
    }
}
