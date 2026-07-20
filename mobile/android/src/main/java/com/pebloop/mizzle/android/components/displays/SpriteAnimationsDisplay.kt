package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.pebloop.mizzle.android.activities.EditorComponentEditorActivity
import com.pebloop.mizzle.data.components.SpriteAnimationData
import com.pebloop.mizzle.data.components.data.DataComponent

class SpriteAnimationsDisplay : ComponentDisplay<ArrayList<SpriteAnimationData>> {
    override fun getId(): String = "ANIMATIONS"

    override fun getDisplay(
        context: Context,
        data: Map.Entry<String, DataComponent<ArrayList<SpriteAnimationData>>>
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

        button.text = "Edit Animations"
        button.setOnClickListener {
            if (context is EditorComponentEditorActivity) {
                context.launchAnimationListEditor(data.key, data.value.data)
            }
        }

        linear.addView(label)
        linear.addView(info)
        linear.addView(button)

        return linear
    }
}
