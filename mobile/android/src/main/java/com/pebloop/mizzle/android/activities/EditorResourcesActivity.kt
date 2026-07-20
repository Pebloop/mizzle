package com.pebloop.mizzle.android.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pebloop.mizzle.R
import com.pebloop.mizzle.android.util.DropletPersistence
import com.pebloop.mizzle.data.DropletData
import com.pebloop.mizzle.data.ResourceData
import android.widget.Toast
import com.pebloop.mizzle.android.auth.AuthManager
import com.pebloop.mizzle.android.auth.ServerResourceItem
import java.io.File
import java.io.InputStream

class EditorResourcesActivity : AppCompatActivity() {

    var droplet: DropletData? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingUri: String? = null

    private var pendingResource: ResourceData? = null
    private var isTexturePending: Boolean = true

    private val tilesetEditorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            @Suppress("UNCHECKED_CAST")
            val generated = result.data?.getSerializableExtra("resources", ArrayList::class.java) as? ArrayList<ResourceData>
            if (generated != null) {
                droplet?.textures = (droplet?.textures ?: arrayOf()) + generated.toTypedArray()
                refreshLists()
            }
        }
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pendingResource?.path = it.toString()
            // We might want to update the dialog UI here if we were showing it,
            // but for simplicity, the path is just updated in the object.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resources)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resources_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        droplet = intent.getSerializableExtra("droplet", DropletData::class.java)

        val addTextureButton: ImageButton = findViewById(R.id.add_texture_button)
        val addAudioButton: ImageButton = findViewById(R.id.add_audio_button)

        refreshLists()

        addTextureButton.setOnClickListener {
            showResourceDialog(null, true)
        }

        addAudioButton.setOnClickListener {
            showResourceDialog(null, false)
        }
    }

    private fun showResourceDialog(resource: ResourceData?, isTexture: Boolean) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_resource, null)
        val nameInput: EditText = dialogView.findViewById(R.id.resource_name_input)
        val pathText: TextView = dialogView.findViewById(R.id.resource_path_text)
        val pickButton: MaterialButton = dialogView.findViewById(R.id.pick_file_button)
        val serverLibButton: MaterialButton = dialogView.findViewById(R.id.server_library_button)

        val workingResource = resource ?: ResourceData("New Resource", "")
        nameInput.setText(workingResource.name)
        pathText.text = if (workingResource.path.isEmpty()) "No file selected" else workingResource.path

        pickButton.setOnClickListener {
            pendingResource = workingResource
            isTexturePending = isTexture
            filePickerLauncher.launch(if (isTexture) "image/*" else "audio/*")
        }

        serverLibButton.setOnClickListener {
            val typeFilter = if (isTexture) "texture" else "audio"
            Toast.makeText(this, "Fetching server resource library...", Toast.LENGTH_SHORT).show()
            AuthManager.getInstance(this).fetchServerResources(typeFilter, object : AuthManager.AuthCallback<List<ServerResourceItem>> {
                override fun onSuccess(result: List<ServerResourceItem>) {
                    if (result.isEmpty()) {
                        Toast.makeText(this@EditorResourcesActivity, "No ${typeFilter}s found in server library", Toast.LENGTH_LONG).show()
                        return
                    }

                    val names = result.map { it.name }.toTypedArray()
                    MaterialAlertDialogBuilder(this@EditorResourcesActivity)
                        .setTitle("Select ${if (isTexture) "Texture" else "Audio"} from Server")
                        .setItems(names) { _, which ->
                            val selectedItem = result[which]
                            val ext = if (selectedItem.type == "audio") ".mp3" else ".png"
                            val targetFile = File(filesDir, "server_resources/${selectedItem.id}$ext")

                            Toast.makeText(this@EditorResourcesActivity, "Downloading ${selectedItem.name}...", Toast.LENGTH_SHORT).show()
                            AuthManager.getInstance(this@EditorResourcesActivity).downloadServerResourceFile(
                                selectedItem.downloadUrl,
                                targetFile,
                                object : AuthManager.AuthCallback<File> {
                                    override fun onSuccess(downloadedFile: File) {
                                        workingResource.name = selectedItem.name
                                        workingResource.path = Uri.fromFile(downloadedFile).toString()
                                        workingResource.regionX = selectedItem.regionX
                                        workingResource.regionY = selectedItem.regionY
                                        workingResource.regionWidth = selectedItem.regionWidth
                                        workingResource.regionHeight = selectedItem.regionHeight

                                        nameInput.setText(selectedItem.name)
                                        pathText.text = workingResource.path
                                        Toast.makeText(this@EditorResourcesActivity, "Downloaded successfully!", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onError(errorMessage: String) {
                                        Toast.makeText(this@EditorResourcesActivity, "Download failed: $errorMessage", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(this@EditorResourcesActivity, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                }
            })
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(if (resource == null) "Add Resource" else "Edit Resource")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                workingResource.name = nameInput.text.toString()
                if (resource == null) {
                    if (isTexture) {
                        droplet?.textures = (droplet?.textures ?: arrayOf()) + workingResource
                    } else {
                        droplet?.audios = (droplet?.audios ?: arrayOf()) + workingResource
                    }
                }
                refreshLists()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun refreshLists() {
        val texturesList: LinearLayout = findViewById(R.id.textures_list)
        val audiosList: LinearLayout = findViewById(R.id.audios_list)

        texturesList.removeAllViews()
        audiosList.removeAllViews()

        droplet?.textures?.forEachIndexed { index, resource ->
            texturesList.addView(createResourceItem(resource, true, index))
        }

        droplet?.audios?.forEachIndexed { index, resource ->
            audiosList.addView(createResourceItem(resource, false, index))
        }
    }

    private fun createResourceItem(resource: ResourceData, isTexture: Boolean, index: Int): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_resource, null)
        val preview: ImageView = view.findViewById(R.id.resource_preview)
        val name: TextView = view.findViewById(R.id.resource_name)
        val path: TextView = view.findViewById(R.id.resource_path)
        val playButton: ImageButton = view.findViewById(R.id.resource_play_button)
        val splitButton: ImageButton = view.findViewById(R.id.resource_split_button)
        val editButton: ImageButton = view.findViewById(R.id.resource_edit_button)
        val deleteButton: ImageButton = view.findViewById(R.id.resource_delete_button)

        name.text = resource.name
        path.text = resource.path

        if (isTexture) {
            splitButton.visibility = View.VISIBLE
            splitButton.setOnClickListener {
                val intent = Intent(this, EditorTilesetEditorActivity::class.java)
                intent.putExtra("resource", resource)
                tilesetEditorLauncher.launch(intent)
            }
            if (resource.path.isNotEmpty()) {
                try {
                    val uri = Uri.parse(resource.path)
                    val inputStream: InputStream? = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    preview.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    preview.setImageResource(R.drawable.icon_mizzle)
                }
            } else {
                preview.setImageResource(R.drawable.icon_mizzle)
            }
        } else {
            preview.setImageResource(R.drawable.play_button) // Just a placeholder icon for audio
            playButton.visibility = View.VISIBLE
            playButton.setOnClickListener {
                toggleAudio(resource.path)
            }
        }

        editButton.setOnClickListener {
            showResourceDialog(resource, isTexture)
        }

        deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete Resource")
                .setMessage("Are you sure you want to delete ${resource.name}?")
                .setPositiveButton("Delete") { _, _ ->
                    if (isTexture) {
                        droplet?.textures = droplet?.textures?.filterIndexed { i, _ -> i != index }?.toTypedArray() ?: arrayOf()
                    } else {
                        droplet?.audios = droplet?.audios?.filterIndexed { i, _ -> i != index }?.toTypedArray() ?: arrayOf()
                    }
                    refreshLists()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return view
    }

    private fun toggleAudio(uriString: String) {
        if (uriString.isEmpty()) return

        if (mediaPlayer?.isPlaying == true && currentPlayingUri == uriString) {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            currentPlayingUri = null
            return
        }

        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer = mediaPlayer ?: MediaPlayer()
            mediaPlayer?.setDataSource(this, Uri.parse(uriString))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            currentPlayingUri = uriString
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun finish() {
        droplet?.let {
            DropletPersistence.saveDroplet(this, it)
        }
        intent.putExtra("droplet", droplet)
        setResult(RESULT_OK, intent)
        super.finish()
    }
}
