package com.example.food

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.food.databinding.ActivitySigninBinding
import com.example.food.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SigninActivity : AppCompatActivity() {

    private lateinit var email : String
    private lateinit var password : String
    private lateinit var username : String
    private lateinit var auth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var googleSignInClient : GoogleSignInClient

    private val binding : ActivitySigninBinding by lazy {
        ActivitySigninBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        auth= Firebase.auth
        database = Firebase.database.reference
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)

        binding.createAccountButton.setOnClickListener{
            username = binding.userName.text.toString()
            email = binding.emailAddress.text.toString().trim()
            password = binding.password.text.toString().trim()

            if(username.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please fill all the details!", Toast.LENGTH_SHORT).show()
            }
            else{
                if(password.length < 6){
                    Toast.makeText(this, "Password must contain atleast 6 characters!", Toast.LENGTH_SHORT).show()
                }
                createAccount(email,password)
            }
        }

        binding.button7.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.googleButton.setOnClickListener{
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account : GoogleSignInAccount?=task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if(authTask.isSuccessful){

                        Toast.makeText(this,"Successfully signed in with Google!",Toast.LENGTH_LONG).show()
                        updateUi(authTask.result?.user)

                    }
                    else{
                        Toast.makeText(this, "Sign-in failed!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        else{
            Toast.makeText(this, "Sign-in failed!", Toast.LENGTH_LONG).show()
        }
    }


    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            task->
            if(task.isSuccessful){
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, "Account creation failed!", Toast.LENGTH_SHORT).show()
                Log.d("Account","createAccount: Failure",task.exception)
            }
        }
    }

    private fun saveUserData() {
        username = binding.userName.text.toString()
        password = binding.password.text.toString().trim()
        email = binding.emailAddress.text.toString().trim()
        val user =UserModel(username,email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).setValue(user)
    }
}