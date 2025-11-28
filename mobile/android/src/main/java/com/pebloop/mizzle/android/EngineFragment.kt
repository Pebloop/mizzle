package com.pebloop.mizzle.android

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
 * Use the [EngineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EngineFragment : AndroidFragmentApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            super.onCreateView(inflater, container, savedInstanceState)
            val configuration = AndroidApplicationConfiguration()
            val gameEngine: View? = initializeForView(Main(), configuration)
            return gameEngine
    }
}
