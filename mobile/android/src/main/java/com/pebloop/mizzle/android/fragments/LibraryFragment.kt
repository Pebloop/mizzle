package com.pebloop.mizzle.android.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
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

        val newDropletButton: FloatingActionButton? = view.findViewById(R.id.newDropplet)
        newDropletButton?.setOnClickListener {
            val intent = Intent(activity, EditorActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}
