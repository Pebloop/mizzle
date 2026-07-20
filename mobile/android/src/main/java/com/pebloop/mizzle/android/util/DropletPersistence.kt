package com.pebloop.mizzle.android.util

import android.content.Context
import android.util.Log
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.pebloop.mizzle.android.auth.AuthManager
import com.pebloop.mizzle.data.DropletData
import java.io.File

object DropletPersistence {
    private const val TAG = "DropletPersistence"
    private const val DROPLETS_DIR = "droplets"

    private val json = Json().apply {
        setOutputType(JsonWriter.OutputType.json)
        setIgnoreUnknownFields(true)
    }

    fun toJson(droplet: DropletData): String {
        return try {
            json.toJson(droplet)
        } catch (e: Exception) {
            Log.e(TAG, "Error serializing droplet to JSON", e)
            ""
        }
    }

    fun saveDroplet(context: Context, droplet: DropletData): Boolean {
        return try {
            val dir = File(context.filesDir, DROPLETS_DIR)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val jsonFile = File(dir, "${droplet.id}.json")
            val jsonStr = json.toJson(droplet)
            jsonFile.writeText(jsonStr)
            Log.d(TAG, "Droplet saved successfully: ${jsonFile.absolutePath}")

            // Automatically sync to server on each save
            try {
                val authManager = AuthManager.getInstance(context)
                authManager.uploadDroplet(droplet.id, droplet.name, jsonStr, object : AuthManager.AuthCallback<String> {
                    override fun onSuccess(result: String) {
                        Log.d(TAG, "Droplet synced to server successfully: ${droplet.id}")
                    }

                    override fun onError(errorMessage: String) {
                        Log.w(TAG, "Droplet server sync: $errorMessage")
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "Error initiating server sync for droplet", e)
            }

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
            if (!dir.exists()) return emptyList()

            val files = dir.listFiles() ?: return emptyList()

            files.filter { it.extension == "json" }.forEach { file ->
                try {
                    val text = file.readText()
                    if (text.isNotBlank()) {
                        val droplet = json.fromJson(DropletData::class.java, text)
                        if (droplet != null) {
                            droplets.add(droplet)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading droplet JSON file: ${file.name}", e)
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
            if (serFile.exists()) serFile.delete()

            if (jsonFile.exists()) {
                jsonFile.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting droplet: $dropletId", e)
            false
        }
    }
}
