package com.example.chatapp

//import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
//import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtName:EditText
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText
    private lateinit var btnSignUp : Button
    private lateinit var mAuth: FirebaseAuth     //firebase authitication
    private lateinit var mDbRef: DatabaseReference   //for get refernece of firebase realtime database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //intialize every view component
        mAuth = FirebaseAuth.getInstance()
        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignUp)

        //when signup button pressed then perform this action
        btnSignUp.setOnClickListener {
            //get information from edittext
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            //call this function when user first time register
            signUp(name,email,password)
        }
    }

    private fun signUp(name:String,email: String, password: String) {
         //logic of creating user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success

                    //code function for adding user to database
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!) //make it null safe->!!

                    //code for jumping to home
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    finish()   //for finishing previous activity
                    startActivity(intent)


                } else {
                    // Sign in fails
                    val exception = task.exception
                    if (exception != null) {
                        // Handle specific exceptions, e.g., FirebaseAuthInvalidCredentialsException
                        Toast.makeText(this@SignUp, exception.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SignUp, "Some error Occurred", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        //logic for adding user to database
        //first initial database from fiberbase itself

        //initialize realtime database
        mDbRef = FirebaseDatabase.getInstance().getReference()

        //now add user to it(mDbRef) realtime database
        mDbRef.child("user").child(uid).setValue(User(name,email,uid))  //this will create parent node and then child node


    }
}