package com.pebloop.mizzle.android.components.displays

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.ResourceData
import com.pebloop.mizzle.data.components.data.DataComponent

class TextureDisplay : ComponentDisplay<String> {
    override fun getId(): String = "TEXTURE"

    override fun getDisplay(
        context: Context,
        data: Map.Entry<String, DataComponent<String>>
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

        val textures = Main.getInstance().droplet.textures
        val items = arrayOf(ResourceData("None", "")) + textures

        val adapter = object : ArrayAdapter<ResourceData>(
            context,
            R.layout.item_spinner_texture,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createViewFromResource(position, convertView, parent)
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createViewFromResource(position, convertView, parent)
            }

            private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_spinner_texture, parent, false)
                val preview: ImageView = view.findViewById(R.id.spinner_texture_preview)
                val name: TextView = view.findViewById(R.id.spinner_texture_name)

                val item = getItem(position)!!
                name.text = item.name

                if (item.path.isNotEmpty()) {
                    try {
                        val uri = Uri.parse(item.path)
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        preview.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        preview.setImageResource(R.drawable.icon_mizzle)
                    }
                } else {
                    preview.setImageResource(R.drawable.icon_mizzle)
                }

                return view
            }
        }

        spinner.adapter = adapter

        // Set current selection
        val currentName = data.value.data
        val currentIndex = items.indexOfFirst { it.name == currentName }
        if (currentIndex != -1) {
            spinner.setSelection(currentIndex)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = items[position]
                data.value.data = if (selected.name == "None") "" else selected.name
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        linear.addView(label)
        linear.addView(spinner)

        return linear
    }
}
