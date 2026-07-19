package com.pebloop.mizzle.android.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.components.Components


class EditorAddComponentActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_component)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        val closeButton: ImageButton = findViewById(R.id.add_component_close_button)
        val componentContainer: LinearLayout = findViewById(R.id.add_components_container)

        closeButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        for (component in Components.list) {
            val button: Button = Button(baseContext)
            button.text = component.getDisplayName()
            button.setOnClickListener {
                intent.putExtra("component", component)
                setResult(RESULT_OK, intent)
                finish()
            }
            componentContainer.addView(button)
        }
    }

    override fun exit() {

    }
}
