package com.pebloop.mizzle.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.pebloop.mizzle.Main

/**
 * A simple [Fragment] subclass.
 * Use the [EditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditorFragment : AndroidFragmentApplication() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            val configuration = AndroidApplicationConfiguration()
            configuration.useImmersiveMode = false
            val gameEngine: View? = initializeForView(Main(Main.Launcher.EDITOR), configuration)
            return gameEngine
    }
}
