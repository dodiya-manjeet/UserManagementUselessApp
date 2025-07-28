// Student ID: 2023eb03183
package com.example.usermanagementapp_2023eb03183

import java.io.Serializable

// Data class to hold user information.
// It implements Serializable to be passed in Intents.
data class User(
    val name: String = "",
    val email: String = "",
    val role: String = ""
) : Serializable