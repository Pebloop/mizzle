package com.pebloop.mizzle.android.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pebloop.mizzle.R
import com.pebloop.mizzle.android.activities.EditorActivity

/**
 * A simple [Fragment] subclass.
 * Use the [LibraryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibraryFragment : Fragment(R.layout.fragment_library) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        val newDroppletButton: FloatingActionButton? = view.findViewById(R.id.newDropplet)
        newDroppletButton?.setOnClickListener {
            Log.d("LibraryFragment", "New Dropplet button clicked")
            val intent = Intent(activity, EditorActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}
