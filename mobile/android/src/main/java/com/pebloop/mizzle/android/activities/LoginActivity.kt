package com.pebloop.mizzle.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pebloop.mizzle.R
import com.pebloop.mizzle.android.AndroidLauncher
import com.pebloop.mizzle.android.auth.AuthManager
import com.pebloop.mizzle.android.auth.UserSession

class LoginActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    private lateinit var titleTextView: TextView
    private lateinit var subtitleTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var serverUrlEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var offlineModeButton: Button
    private lateinit var toggleModeTextView: TextView
    private lateinit var progressBar: ProgressBar

    private var isSignUpMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authManager = AuthManager.getInstance(this)

        titleTextView = findViewById(R.id.titleTextView)
        subtitleTextView = findViewById(R.id.subtitleTextView)
        errorTextView = findViewById(R.id.errorTextView)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        serverUrlEditText = findViewById(R.id.serverUrlEditText)
        submitButton = findViewById(R.id.submitButton)
        offlineModeButton = findViewById(R.id.offlineModeButton)
        toggleModeTextView = findViewById(R.id.toggleModeTextView)
        progressBar = findViewById(R.id.progressBar)

        serverUrlEditText.setText(authManager.serverUrl)

        updateUiMode()

        toggleModeTextView.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateUiMode()
        }

        submitButton.setOnClickListener {
            performAuth()
        }

        offlineModeButton.setOnClickListener {
            authManager.signInOffline(object : AuthManager.AuthCallback<UserSession> {
                override fun onSuccess(result: UserSession) {
                    onAuthSuccess()
                }

                override fun onError(errorMessage: String) {
                    showError(errorMessage)
                }
            })
        }
    }

    private fun updateUiMode() {
        errorTextView.visibility = View.GONE
        if (isSignUpMode) {
            titleTextView.text = "Create Account"
            subtitleTextView.text = "Sign up for a new Mizzle account"
            nameEditText.visibility = View.VISIBLE
            submitButton.text = "Sign Up"
            toggleModeTextView.text = "Already have an account? Sign In"
        } else {
            titleTextView.text = "Welcome to Mizzle"
            subtitleTextView.text = "Sign in to your user account"
            nameEditText.visibility = View.GONE
            submitButton.text = "Sign In"
            toggleModeTextView.text = "Don't have an account? Sign Up"
        }
    }

    private fun performAuth() {
        val serverUrl = serverUrlEditText.text.toString().trim()
        if (serverUrl.isNotEmpty()) {
            authManager.serverUrl = serverUrl
        }

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter email and password.")
            return
        }

        if (isSignUpMode) {
            val name = nameEditText.text.toString().trim()
            if (name.isEmpty()) {
                showError("Please enter your full name.")
                return
            }
            if (password.length < 8) {
                showError("Password must be at least 8 characters long.")
                return
            }
            setLoading(true)
            authManager.signUp(name, email, password, object : AuthManager.AuthCallback<UserSession> {
                override fun onSuccess(result: UserSession) {
                    setLoading(false)
                    onAuthSuccess()
                }

                override fun onError(errorMessage: String) {
                    setLoading(false)
                    showError(errorMessage)
                }
            })
        } else {
            setLoading(true)
            authManager.signIn(email, password, object : AuthManager.AuthCallback<UserSession> {
                override fun onSuccess(result: UserSession) {
                    setLoading(false)
                    onAuthSuccess()
                }

                override fun onError(errorMessage: String) {
                    setLoading(false)
                    showError(errorMessage)
                }
            })
        }
    }

    private fun onAuthSuccess() {
        val intent = Intent(this, AndroidLauncher::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        submitButton.isEnabled = !loading
        toggleModeTextView.isEnabled = !loading
    }
}
