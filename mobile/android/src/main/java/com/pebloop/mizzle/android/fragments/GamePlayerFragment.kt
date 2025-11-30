package com.pebloop.mizzle.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.R

/**
 * A simple [Fragment] subclass.
 * Use the [GamePlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GamePlayerFragment : AndroidFragmentApplication() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val config = AndroidApplicationConfiguration()
        config.useImmersiveMode = false
        return initializeForView(Main(Main.Launcher.GAME), config)
    }

}
