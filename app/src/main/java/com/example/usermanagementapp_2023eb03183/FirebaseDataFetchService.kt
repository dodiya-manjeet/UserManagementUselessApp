// Student ID: 2023eb03183
package com.example.usermanagementapp_2023eb03183

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.database.*

// Service to fetch all user data from Firebase in the background.
class FirebaseDataFetchService : Service() {

    private lateinit var database: DatabaseReference

    companion object {
        const val ACTION_USERS_FETCHED = "com.example.usermanagementapp_2023eb03183.USERS_FETCHED"
        const val EXTRA_USERS_LIST = "extra_users_list"
    }

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance().getReference("Users")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Query Firebase for all users.
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                sendUsersBroadcast(userList)
                stopSelf()
            }

            override fun onCancelled(error: DatabaseError) {
                stopSelf()
            }
        })
        return START_NOT_STICKY
    }

    private fun sendUsersBroadcast(userList: ArrayList<User>) {
        val intent = Intent(ACTION_USERS_FETCHED).apply {
            putExtra(EXTRA_USERS_LIST, userList)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}