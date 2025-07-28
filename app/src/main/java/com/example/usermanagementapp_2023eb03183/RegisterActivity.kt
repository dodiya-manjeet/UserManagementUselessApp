// Student ID: 2023eb03183
package com.example.usermanagementapp_2023eb03183

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.usermanagementapp_2023eb03183.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Setup the user type spinner
        val userTypes = arrayOf("normal", "admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userTypes)
        binding.spinnerUserType.adapter = adapter

        // Register button click listener
        binding.buttonRegister.setOnClickListener {
            registerUser()
        }

        // Link to LoginActivity click listener
        binding.textViewLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    private fun registerUser() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show()
            return
        }
        val email = binding.editTextUsername.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        val userType = binding.spinnerUserType.selectedItem.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    firebaseUser?.let {
                        val ref = database.getReference("Users").child(it.uid)
                        val userData = mapOf("email" to email, "role" to userType, "name" to "")
                        ref.setValue(userData).addOnSuccessListener {
                            Toast.makeText(this, "User $email is registered", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
        }
    }
}