package com.example.chat_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat_app.adapter.MessageRecyclerAdapter
import com.example.chat_app.databinding.ActivityChatBinding
import com.example.chat_app.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: MessageRecyclerAdapter
    private val list: MutableList<Message> = mutableListOf()
    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private val ref = FirebaseDatabase.getInstance().reference
    private val senderUid = FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username")
        binding.toolbar.title = username
        setSupportActionBar(binding.toolbar)

        displayChatMessages()

        binding.sendMessageButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun displayChatMessages() {
        val receiverUid = intent.getStringExtra("uid")

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        val messageAdapter = MessageRecyclerAdapter(layoutInflater, list)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = messageAdapter

        ref.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    snapshot.children.forEach() {
                        val message = it.getValue(Message::class.java)
                        list.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    private fun sendMessage() {
        val messageText = binding.enterMessage.text.toString()
        val message = Message(messageText, senderUid!!)
        ref.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(message)
            .addOnSuccessListener {
                ref.child("chats").child(receiverRoom!!).child("messages").push()
                    .setValue(message)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

