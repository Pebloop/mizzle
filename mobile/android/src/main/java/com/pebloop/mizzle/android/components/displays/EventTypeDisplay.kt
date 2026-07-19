package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.view.View
import android.widget.*
import com.pebloop.mizzle.data.components.EventType
import com.pebloop.mizzle.data.components.data.DataComponent

class EventTypeDisplay : ComponentDisplay<EventType> {
    override fun getId(): String = "EVENT_TYPE"

    override fun getDisplay(
        context: Context,
        data: Map.Entry<String, DataComponent<EventType>>
    ): View {
        val linear = LinearLayout(context)
        val label = TextView(context)
        val spinner = Spinner(context)

        label.text = data.key

        linear.orientation = LinearLayout.VERTICAL
        linear.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            EventType.values()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set current selection
        val currentIndex = EventType.values().indexOf(data.value.data)
        if (currentIndex != -1) {
            spinner.setSelection(currentIndex)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                data.value.data = EventType.values()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        linear.addView(label)
        linear.addView(spinner)

        return linear
    }
}
