package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.pebloop.mizzle.android.activities.EditorComponentEditorActivity
import com.pebloop.mizzle.data.components.TimelineAnimationData
import com.pebloop.mizzle.data.components.data.DataComponent

class TimelineAnimationsDisplay : ComponentDisplay<ArrayList<TimelineAnimationData>> {
    override fun getId(): String = "TIMELINE_ANIMATIONS"

    override fun getDisplay(
        context: Context,
        data: Map.Entry<String, DataComponent<ArrayList<TimelineAnimationData>>>
    ): View {
        val linear = LinearLayout(context)
        val label = TextView(context)
        val info = TextView(context)
        val button = Button(context)

        label.text = data.key
        info.text = "${data.value.data.size} Animations"

        linear.orientation = LinearLayout.VERTICAL
        linear.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        button.text = "Edit Timeline Animations"
        button.setOnClickListener {
            // Future implementation for timeline editor
            // if (context is EditorComponentEditorActivity) {
            //    context.launchTimelineAnimationEditor(data.key, data.value.data)
            // }
        }

        linear.addView(label)
        linear.addView(info)
        linear.addView(button)

        return linear
    }
}
