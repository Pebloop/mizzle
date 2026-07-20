package com.pebloop.mizzle.android.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.pebloop.mizzle.R
import com.pebloop.mizzle.android.util.DropletPersistence
import com.pebloop.mizzle.data.DropletData

class EditorActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    var droplet: DropletData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        droplet = intent.getSerializableExtra("droplet", DropletData::class.java)

        setContentView(R.layout.activity_editor)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }

    override fun onPause() {
        super.onPause()
        droplet?.let {
            DropletPersistence.saveDroplet(this, it)
        }
    }

    override fun exit() {

    }
}
