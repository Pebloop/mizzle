package com.pebloop.mizzle.android.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pebloop.mizzle.R
import com.pebloop.mizzle.android.activities.LoginActivity
import com.pebloop.mizzle.android.auth.AuthManager

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        authManager = AuthManager.getInstance(requireContext())

        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
        val userEmailTextView: TextView = view.findViewById(R.id.userEmailTextView)
        val serverUrlValueTextView: TextView = view.findViewById(R.id.serverUrlValueTextView)
        val statusValueTextView: TextView = view.findViewById(R.id.statusValueTextView)
        val signOutButton: Button = view.findViewById(R.id.signOutButton)

        val user = authManager.currentUser
        if (user != null) {
            userNameTextView.text = user.name
            userEmailTextView.text = user.email
        } else {
            userNameTextView.text = "Developer User"
            userEmailTextView.text = "dev@local.mizzle"
        }

        serverUrlValueTextView.text = authManager.serverUrl
        if (authManager.sessionCookie?.contains("mizzle_offline_session") == true) {
            statusValueTextView.text = "Offline Mode"
            statusValueTextView.setTextColor(0xFF38BDF8.toInt())
        } else {
            statusValueTextView.text = "Online Session"
            statusValueTextView.setTextColor(0xFF34D399.toInt())
        }

        signOutButton.setOnClickListener {
            authManager.signOut(object : AuthManager.AuthCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    navigateToLogin()
                }

                override fun onError(errorMessage: String) {
                    navigateToLogin()
                }
            })
        }

        return view
    }

    private fun navigateToLogin() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
