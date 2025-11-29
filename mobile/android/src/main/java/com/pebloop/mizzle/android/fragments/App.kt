package com.pebloop.mizzle.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pebloop.mizzle.R

/**
 * A simple [Fragment] subclass.
 * Use the [App.newInstance] factory method to
 * create an instance of this fragment.
 */
class App : Fragment(R.layout.fragment_app) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app, container, false)

        val bnv: BottomNavigationView? = view.findViewById(R.id.bottomNavigationView)
        val playFragment = PlayFragment()
        val libraryFragment = LibraryFragment()

        setCurrentFragment(playFragment)
        bnv?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.play -> {
                    setCurrentFragment(playFragment)
                }
                R.id.library -> {
                    setCurrentFragment(libraryFragment)
                }
            }
            true
        }



        return view
    }

    private fun setCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.commit {
            replace(R.id.appFragment, fragment)
            addToBackStack(null)
        }
    }

}
