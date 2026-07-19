package com.pebloop.mizzle.android.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.R
import com.pebloop.mizzle.android.activities.PlayActivity
import com.pebloop.mizzle.android.util.DropletPersistence
import com.pebloop.mizzle.data.DropletData
import com.pebloop.mizzle.editor.EditorActionsExtern

/**
 * A simple [Fragment] subclass.
 * Use the [GamePlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GamePlayerFragment : AndroidFragmentApplication() {

    var engine: Main? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val config = AndroidApplicationConfiguration()
        config.useImmersiveMode = false
        val droplet: DropletData = arguments?.getSerializable("droplet", DropletData::class.java)
            ?: (parentFragment as? PlayFragment)?.arguments?.getSerializable("droplet", DropletData::class.java)
            ?: DropletData()
        val showUpload = arguments?.getBoolean("showUpload", true)
            ?: (parentFragment as? PlayFragment)?.arguments?.getBoolean("showUpload", true)
            ?: true

        var actions: EditorActionsExtern? = null
        if (activity is PlayActivity) {
            actions = EditorActionsExtern({ _, _ -> }, {
                (activity as PlayActivity).exit()
            }, {
                val success = DropletPersistence.saveDroplet(requireContext(), droplet)
                if (success) {
                    Toast.makeText(context, "Droplet saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to save droplet.", Toast.LENGTH_SHORT).show()
                }
            }, {}, showUpload)
        }

        engine = Main(Main.Launcher.GAME, droplet, actions)
        return initializeForView(engine, config)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        engine?.dispose()
    }

}
