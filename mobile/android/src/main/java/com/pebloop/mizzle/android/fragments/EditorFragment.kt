package com.pebloop.mizzle.android.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.android.activities.EditorActivity
import com.pebloop.mizzle.android.activities.EditorEntityEditorActivity
import com.pebloop.mizzle.data.DropletData
import com.pebloop.mizzle.data.EntityData
import com.pebloop.mizzle.editor.EditorActions
import com.pebloop.mizzle.editor.EditorActionsExtern

/**
 * A simple [Fragment] subclass.
 * Use the [EditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditorFragment : AndroidFragmentApplication() {

    var allEditorActions: EditorActions? = null

    val entityEditorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val newEntity = intent!!.getSerializableExtra("entity", EntityData::class.java)
            Log.d("test", "testts")
            allEditorActions?.updateEntity(newEntity!!)
        }
    }

    fun openEntityEditor(entity: EntityData, actions: EditorActions) {
        allEditorActions = actions
        val intent = Intent(activity, EditorEntityEditorActivity::class.java)
        intent.putExtra("entity", entity)
        entityEditorLauncher.launch(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actions: EditorActionsExtern = EditorActionsExtern(::openEntityEditor)
        val configuration = AndroidApplicationConfiguration()
        configuration.useImmersiveMode = false
        val gameEngine: View? = initializeForView(Main(Main.Launcher.EDITOR, (activity as EditorActivity).droplet, actions), configuration)
        return gameEngine
    }
}
