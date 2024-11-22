package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    //recyclerview user
    private lateinit var userRecyclerView: RecyclerView
    //recyclerview user need array list and adapter
    private lateinit var userList: ArrayList<User> //this is aaraylist of user type
    private lateinit var adapter: UserAdapter  //this is adapter of type userAdapter
    private lateinit var mAuth: FirebaseAuth  //firebase reference
    private lateinit var mDbRef : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //now intial eveythings
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()
        adapter = UserAdapter(this,userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        //get inside the database and read the database
        mDbRef.child("user").addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear previous list
                userList.clear()

                //snapshot take schema of database
                for(postSnapshot in snapshot.children){

                    val currentUser = postSnapshot.getValue(User::class.java) //postsnapshot for getting users from database
                    if(mAuth.currentUser?.uid!= currentUser?.uid)
                    {
                        userList.add(currentUser!!)  //for adding users to ths list,!!->make null shape

                    }



                }
                adapter.notifyDataSetChanged()

            }



            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //add click listner to the menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout)
        {
            //write logic for logout
            mAuth.signOut()
            //go to login page
            val intent = Intent(this@MainActivity,Login::class.java)
            finish()
            startActivity(intent)

            return true
        }
        return false

    }

}