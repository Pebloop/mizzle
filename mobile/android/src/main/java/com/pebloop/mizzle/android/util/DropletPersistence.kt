package com.pebloop.mizzle.android.util

import android.content.Context
import android.util.Log
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.pebloop.mizzle.data.DropletData
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object DropletPersistence {
    private const val TAG = "DropletPersistence"
    private const val DROPLETS_DIR = "droplets"

    private val json = Json().apply {
        setOutputType(JsonWriter.OutputType.json)
    }

    fun saveDroplet(context: Context, droplet: DropletData): Boolean {
        return try {
            val dir = File(context.filesDir, DROPLETS_DIR)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, "${droplet.id}.json")
            file.writeText(json.prettyPrint(droplet))
            Log.d(TAG, "Droplet saved successfully: ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving droplet", e)
            false
        }
    }

    fun getAllDroplets(context: Context): List<DropletData> {
        val droplets = mutableListOf<DropletData>()
        try {
            val dir = File(context.filesDir, DROPLETS_DIR)
            if (!dir.exists()) return droplets

            val files = dir.listFiles() ?: return droplets

            // Load JSON droplets
            files.filter { it.extension == "json" }.forEach { file ->
                try {
                    val droplet = json.fromJson(DropletData::class.java, file.readText())
                    if (droplet != null) droplets.add(droplet)
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading droplet JSON file: ${file.name}", e)
                }
            }

            // Load and migrate legacy .ser droplets
            files.filter { it.extension == "ser" }.forEach { file ->
                var droplet: DropletData? = null
                try {
                    ObjectInputStream(FileInputStream(file)).use {
                        droplet = it.readObject() as? DropletData
                    }
                    if (droplet != null) {
                        droplets.add(droplet)
                        // Migrate to JSON
                        if (saveDroplet(context, droplet)) {
                            file.delete()
                            Log.d(TAG, "Migrated legacy droplet to JSON: ${file.name}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading legacy droplet file: ${file.name}. Deleting incompatible file.", e)
                    // Delete incompatible legacy files to prevent repeated errors
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error listing droplets", e)
        }
        return droplets
    }

    fun deleteDroplet(context: Context, dropletId: String): Boolean {
        return try {
            val dir = File(context.filesDir, DROPLETS_DIR)
            val jsonFile = File(dir, "$dropletId.json")
            val serFile = File(dir, "$dropletId.ser")

            var deleted = false
            if (jsonFile.exists()) deleted = jsonFile.delete() || deleted
            if (serFile.exists()) deleted = serFile.delete() || deleted

            deleted
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting droplet: $dropletId", e)
            false
        }
    }
}
