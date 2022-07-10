package com.example.chat_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.R
import com.example.chat_app.model.User
import com.squareup.picasso.Picasso


class UserRecyclerAdapter(
    private val inflater: LayoutInflater,
    private val onClick: (User) -> Unit
) :
    ListAdapter<User, UserRecyclerAdapter.ViewHolder>(UserDiffCallback) {

    class ViewHolder(
        view: View,
        val onClick: (User) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val email = view.findViewById<TextView>(R.id.newMessageUserUsername)
        private val image = view.findViewById<ImageView>(R.id.newMessageUserImage)
        private var user: User? = null

        init {
            view.setOnClickListener {
                user?. let {
                    onClick(it)
                }
            }
        }

        fun bind(user: User) {
            this.user = user
            email.text = user.email
            Picasso
                .get()
                .load(user.profileImageUrl)
                .into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.user_items, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    object UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {

            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {

            return oldItem.uid == newItem.uid && oldItem.username == newItem.username
        }
    }
}