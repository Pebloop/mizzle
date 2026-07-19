package com.pebloop.mizzle.android.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.DropletData
import com.pebloop.mizzle.android.fragments.PlayFragment

class PlayActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_play)

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        val droplet = intent.getSerializableExtra("droplet", DropletData::class.java)
        val showUpload = intent.getBooleanExtra("showUpload", true)
        if (droplet != null) {
            val playFragment = supportFragmentManager.findFragmentById(R.id.play_fragment_container) as? PlayFragment
            val bundle = Bundle()
            bundle.putSerializable("droplet", droplet)
            bundle.putBoolean("showUpload", showUpload)
            playFragment?.arguments = bundle
        }
    }

    override fun exit() {
        finish()
    }
}
