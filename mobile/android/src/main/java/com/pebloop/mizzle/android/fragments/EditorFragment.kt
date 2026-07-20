package com.pebloop.mizzle.android.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.pebloop.mizzle.Main
import com.pebloop.mizzle.android.activities.EditorActivity
import com.pebloop.mizzle.android.activities.EditorEntityEditorActivity
import com.pebloop.mizzle.android.activities.EditorResourcesActivity
import com.pebloop.mizzle.android.activities.PlayActivity
import com.pebloop.mizzle.android.activities.DropletSettingsActivity
import com.pebloop.mizzle.android.util.TextureLoader
import com.pebloop.mizzle.data.DropletData
import com.pebloop.mizzle.data.EntityData
import com.pebloop.mizzle.editor.EditorActions
import com.pebloop.mizzle.editor.EditorActionsExtern
import com.pebloop.mizzle.editor.EditorScreen

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
            val editorScreen = Main.getInstance().screen as? EditorScreen
            editorScreen?.updateEntity(newEntity!!)
        }
    }

    val dropletSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val newDroplet = intent!!.getSerializableExtra("droplet", DropletData::class.java)
            val currentDroplet = (activity as EditorActivity).droplet
            currentDroplet?.updateFrom(newDroplet!!)
            // Re-link textures in case they were lost or the instance changed
            TextureLoader.loadDropletTextures(requireContext(), currentDroplet!!, Main.getInstance(), true)
        }
    }

    val resourcesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val newDroplet = intent!!.getSerializableExtra("droplet", DropletData::class.java)
            val currentDroplet = (activity as EditorActivity).droplet
            currentDroplet?.updateFrom(newDroplet!!)
            TextureLoader.loadDropletTextures(requireContext(), currentDroplet!!, Main.getInstance(), true)
        }
    }

    fun openEntityEditor(entity: EntityData, actions: EditorActions) {
        allEditorActions = actions
        val intent = Intent(activity, EditorEntityEditorActivity::class.java)
        intent.putExtra("entity", entity)
        entityEditorLauncher.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        (activity as? EditorActivity)?.droplet?.let {
            TextureLoader.loadDropletTextures(requireContext(), it, Main.getInstance())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actions: EditorActionsExtern = EditorActionsExtern(::openEntityEditor, {
            activity?.finish()
        }, {
            val intent = Intent(activity, PlayActivity::class.java)
            intent.putExtra("droplet", (activity as EditorActivity).droplet)
            startActivity(intent)
        }, { droplet ->
            val intent = Intent(activity, DropletSettingsActivity::class.java)
            intent.putExtra("droplet", droplet)
            dropletSettingsLauncher.launch(intent)
        }, { droplet ->
            val intent = Intent(activity, EditorResourcesActivity::class.java)
            intent.putExtra("droplet", droplet)
            resourcesLauncher.launch(intent)
        })
        val configuration = AndroidApplicationConfiguration()
        configuration.useImmersiveMode = false
        val droplet: DropletData = (activity as? EditorActivity)?.droplet ?: DropletData()
        val gameEngine = Main(Main.Launcher.EDITOR, droplet, actions)
        val view: View? = initializeForView(gameEngine, configuration)

        // Load textures after initialization to ensure Gdx.app is available
        TextureLoader.loadDropletTextures(requireContext(), droplet, gameEngine)

        return view
    }
}
