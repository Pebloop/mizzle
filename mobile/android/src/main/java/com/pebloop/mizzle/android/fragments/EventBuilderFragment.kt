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
 * Use the [EventBuilderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventBuilderFragment : AndroidFragmentApplication() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            super.onCreateView(inflater, container, savedInstanceState)
            val configuration = AndroidApplicationConfiguration()
            val gameEngine: View? = initializeForView(Main(Main.Launcher.EVENT_BUILDER), configuration)
            return gameEngine
    }
}
