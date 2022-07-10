package com.example.chat_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_app.R
import com.example.chat_app.model.Message
import com.google.firebase.auth.FirebaseAuth

class MessageRecyclerAdapter(
    private val inflater: LayoutInflater,
    private val list: MutableList<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view = inflater.inflate(R.layout.receive_message, parent, false)
            return ReceiveViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.sender_message, parent, false)
            return SendViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = list[position]
        if (holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder
            viewHolder.bind(currentMessage)
        } else if (holder.javaClass == ReceiveViewHolder::class.java) {
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.bind(currentMessage)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = list[position]

        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }



    class SendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val sentMessage = itemView.findViewById<TextView>(R.id.textChatSender)
        private val image = itemView.findViewById<ImageView>(R.id.newMessageUserUsername)
        private var message: Message? = null

        fun bind(message: Message) {
            this.message = message
            sentMessage.text = message.message
        }
    }

    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val receiveMessage = itemView.findViewById<TextView>(R.id.textChatReceiver)
        private var message: Message? = null

        fun bind(message: Message) {
            this.message = message
            receiveMessage.text = message.message
        }
    }
}
