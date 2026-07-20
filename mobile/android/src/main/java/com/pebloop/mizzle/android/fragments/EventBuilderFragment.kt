package com.pebloop.mizzle.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.android.activities.EventBuilderActivity
import com.pebloop.mizzle.editor.EditorActionsExtern

class EventBuilderFragment : AndroidFragmentApplication() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = activity as EventBuilderActivity
        val event = activity.event

        val actions = EditorActionsExtern({ _, _ -> }, {
            activity.exit()
        }, {}, { _ -> }, false)

        val configuration = AndroidApplicationConfiguration()
        configuration.useImmersiveMode = false

        return initializeForView(Main(Main.Launcher.EVENT_BUILDER, null, actions, event), configuration)
    }
}
