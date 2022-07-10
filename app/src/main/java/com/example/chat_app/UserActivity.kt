package com.example.chat_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat_app.adapter.UserRecyclerAdapter
import com.example.chat_app.databinding.ActivityUserBinding
import com.example.chat_app.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var adapter: UserRecyclerAdapter
    private val list: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar2)

        checkUserAuth()

        displayUsers()
    }

    private fun checkUserAuth() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun displayUsers() {
        adapter = UserRecyclerAdapter(layoutInflater) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid", it.uid)
            intent.putExtra("username", it.username)
            intent.putExtra("profileImageUrl", it.profileImageUrl)
            startActivity(intent)
        }
        binding.recyclerViewNewMessage.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNewMessage.adapter = adapter
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                snapshot.children.forEach {
                    val currentUser = it.getValue(User::class.java)
                    if (FirebaseAuth.getInstance().uid != currentUser?.uid) {
                        list.add(currentUser!!)
                    }
                }
                adapter.submitList(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSightOut -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        val search = menu.findItem(R.id.appSearchBar)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (newText.isEmpty()) {
                        adapter.submitList(list)
                    } else {
                        val searchUsers: MutableList<User> = mutableListOf()
                        searchUsers.clear()
                        list.forEach {
                            if (it.email.contains(newText)) {
                                searchUsers.add(it)
                            }
                        }
                        adapter.submitList(searchUsers)
                    }
                    return true
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}