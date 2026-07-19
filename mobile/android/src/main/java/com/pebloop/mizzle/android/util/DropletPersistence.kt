package com.pebloop.mizzle.android.util

import android.content.Context
import android.util.Log
import com.pebloop.mizzle.data.DropletData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object DropletPersistence {
    private const val TAG = "DropletPersistence"
    private const val DROPLETS_DIR = "droplets"

    fun saveDroplet(context: Context, droplet: DropletData): Boolean {
        return try {
            val dir = File(context.filesDir, DROPLETS_DIR)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, "${droplet.id}.ser")
            ObjectOutputStream(FileOutputStream(file)).use { it.writeObject(droplet) }
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

            dir.listFiles()?.filter { it.extension == "ser" }?.forEach { file ->
                try {
                    ObjectInputStream(FileInputStream(file)).use {
                        val droplet = it.readObject() as? DropletData
                        if (droplet != null) droplets.add(droplet)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading droplet file: ${file.name}", e)
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
            val file = File(dir, "$dropletId.ser")
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting droplet: $dropletId", e)
            false
        }
    }
}
