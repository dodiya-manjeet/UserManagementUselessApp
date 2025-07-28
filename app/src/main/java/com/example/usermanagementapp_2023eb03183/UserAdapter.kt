// Student ID: 2023eb03183
package com.example.usermanagementapp_2023eb03183

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

// Custom adapter to display a list of users in the admin's ListView.
class UserAdapter(context: Context, users: List<User>) : ArrayAdapter<User>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Reuse or inflate a new view for the list item.
        val itemView = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)

        val currentUser = getItem(position)

        val text1 = itemView.findViewById<TextView>(android.R.id.text1)
        val text2 = itemView.findViewById<TextView>(android.R.id.text2)

        // Bind data to the views.
        text1.text = currentUser?.name?.ifEmpty { "N/A" }
        text2.text = "Email: ${currentUser?.email} | Role: ${currentUser?.role}"

        return itemView
    }
}