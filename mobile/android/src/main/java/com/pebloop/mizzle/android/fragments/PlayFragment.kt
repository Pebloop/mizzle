package com.pebloop.mizzle.android.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentContainerView
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.pebloop.mizzle.R
import com.pebloop.mizzle.data.DropletData

/**
 * A simple [Fragment] subclass.
 * Use the [PlayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayFragment : Fragment(R.layout.fragment_play), AndroidFragmentApplication.Callbacks {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val droplet: DropletData = arguments?.getSerializable("droplet", DropletData::class.java) ?: DropletData()
        val showUpload = arguments?.getBoolean("showUpload", true) ?: true
        if (arguments == null) {
            arguments = Bundle()
        }
        requireArguments().putSerializable("droplet", droplet)
        requireArguments().putBoolean("showUpload", showUpload)

        val view = inflater.inflate(R.layout.fragment_play, container, false)
        return view
    }

    override fun exit() {
    }

}
