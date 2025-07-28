// Student ID: 2023eb0383
package com.example.usermanagementapp_2023eb03183

import android.content.*
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.usermanagementapp_2023eb03183.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var currentUserRole: String? = null

    // BroadcastReceiver to get data from the background service
    private val userBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == FirebaseDataFetchService.ACTION_USERS_FETCHED) {
                val userList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra(FirebaseDataFetchService.EXTRA_USERS_LIST, ArrayList::class.java) as? ArrayList<User>
                } else {
                    @Suppress("DEPRECATION")
                    intent.getSerializableExtra(FirebaseDataFetchService.EXTRA_USERS_LIST) as? ArrayList<User>
                }
                userList?.let {
                    val adapter = UserAdapter(this@WelcomeActivity, it)
                    binding.listViewUsers.adapter = adapter
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        if (firebaseAuth.currentUser == null) {
            redirectToRegister()
            return
        }

        setupWelcomeMessage()
        setupLogoutButton()
        fetchUserRoleAndSetupUI()

        // Register the broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userBroadcastReceiver,
            IntentFilter(FirebaseDataFetchService.ACTION_USERS_FETCHED)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister to prevent memory leaks
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userBroadcastReceiver)
    }

    private fun setupWelcomeMessage() {
        val sharedPref = getSharedPreferences("UserManagementAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("USERNAME", "User")
        binding.textViewWelcome.text = "Welcome, $username!"
    }

    private fun fetchUserRoleAndSetupUI() {
        val userId = firebaseAuth.currentUser!!.uid

        database.child(userId).child("role").get().addOnSuccessListener { dataSnapshot ->
            currentUserRole = dataSnapshot.getValue(String::class.java)
            configureUIForRole()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch user role.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureUIForRole() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show()
            return
        }

        when (currentUserRole) {
            "admin" -> setupAdminView()
            "normal" -> setupNormalUserView()
            else -> Toast.makeText(this, "Unknown user role.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAdminView() {
        binding.layoutUserDetails.visibility = View.GONE
        binding.textViewAdminTitle.visibility = View.VISIBLE
        binding.listViewUsers.visibility = View.VISIBLE
        startService(Intent(this, FirebaseDataFetchService::class.java))
    }

    private fun setupNormalUserView() {
        binding.textViewAdminTitle.visibility = View.GONE
        binding.listViewUsers.visibility = View.GONE
        binding.layoutUserDetails.visibility = View.VISIBLE
        loadCurrentUserDetails()

        binding.buttonSaveDetails.setOnClickListener {
            saveUserDetails()
        }
    }

    private fun loadCurrentUserDetails() {
        val userId = firebaseAuth.currentUser!!.uid
        database.child(userId).get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            user?.let {
                binding.editTextName.setText(it.name)
                binding.editTextEmailDisplay.setText(it.email)
            }
        }
    }

    private fun saveUserDetails() {
        val name = binding.editTextName.text.toString().trim()
        val email = binding.editTextEmailDisplay.text.toString().trim()
        val userId = firebaseAuth.currentUser!!.uid

        if (name.isNotEmpty() && email.isNotEmpty()) {
            val userUpdates = mapOf(
                "name" to name,
                "email" to email
            )
            database.child(userId).updateChildren(userUpdates).addOnSuccessListener {
                Toast.makeText(this, "Details saved successfully!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to save details.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Name and Email cannot be empty.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLogoutButton() {
        binding.buttonLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("UserManagementAppPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                apply()
            }
            firebaseAuth.signOut()
            redirectToRegister()
        }
    }

    private fun redirectToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        // Clear activity stack so user cannot go back to the welcome screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}