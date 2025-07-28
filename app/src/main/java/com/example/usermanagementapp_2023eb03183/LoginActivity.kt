// Student ID: 2023eb03183
package com.example.usermanagementapp_2023eb03183

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.usermanagementapp_2023eb03183.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // If a user is already logged in, go straight to WelcomeActivity
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        binding.buttonLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show()
            return
        }

        val email = binding.editTextUsername.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Sign in with Firebase Auth
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save username in SharedPreferences for session persistence
                    val sharedPref = getSharedPreferences("UserManagementAppPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("USERNAME", email)
                        apply()
                    }
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                    // Navigate to the main screen
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish() // Prevent user from returning to login screen with back button
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
        }
    }
}