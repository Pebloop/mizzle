package com.pebloop.mizzle.android.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pebloop.mizzle.R
import com.pebloop.mizzle.android.activities.EditorActivity
import com.pebloop.mizzle.android.activities.PlayActivity
import com.pebloop.mizzle.android.util.DropletPersistence
import com.pebloop.mizzle.data.DropletData

/**
 * A simple [Fragment] subclass.
 * Use the [LibraryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibraryFragment : Fragment(R.layout.fragment_library) {

    private lateinit var dropletsContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        val newDropletButton: FloatingActionButton? = view.findViewById(R.id.newDropplet)
        newDropletButton?.setOnClickListener {
            val intent = Intent(activity, EditorActivity::class.java)
            intent.putExtra("droplet", DropletData())
            startActivity(intent)
        }

        dropletsContainer = view.findViewById(R.id.library_droplets_container)

        return view
    }

    override fun onResume() {
        super.onResume()
        refreshDroplets()
    }

    private fun refreshDroplets() {
        dropletsContainer.removeAllViews()
        val droplets = DropletPersistence.getAllDroplets(requireContext())
        val inflater = LayoutInflater.from(context)

        for (droplet in droplets) {
            val itemView = inflater.inflate(R.layout.item_library_droplet, dropletsContainer, false)

            val nameText: TextView = itemView.findViewById(R.id.dropletName)
            val playBtn: ImageButton = itemView.findViewById(R.id.playDroplet)
            val editBtn: ImageButton = itemView.findViewById(R.id.editDroplet)
            val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteDroplet)

            nameText.text = droplet.name

            playBtn.setOnClickListener {
                val intent = Intent(activity, PlayActivity::class.java)
                intent.putExtra("droplet", droplet)
                intent.putExtra("showUpload", false)
                startActivity(intent)
            }

            editBtn.setOnClickListener {
                val intent = Intent(activity, EditorActivity::class.java)
                intent.putExtra("droplet", droplet)
                startActivity(intent)
            }

            deleteBtn.setOnClickListener {
                DropletPersistence.deleteDroplet(requireContext(), droplet.id)
                refreshDroplets()
            }

            dropletsContainer.addView(itemView)
        }
    }

}
