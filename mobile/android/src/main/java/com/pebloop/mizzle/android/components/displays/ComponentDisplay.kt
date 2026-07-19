package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.view.View
import com.pebloop.mizzle.data.components.data.DataComponent

interface ComponentDisplay<T> {
    fun getId(): String

    fun getDisplay(context: Context, data: Map.Entry<String, DataComponent<T>>): View
}
