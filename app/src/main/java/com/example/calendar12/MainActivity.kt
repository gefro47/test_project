package com.example.calendar12

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var googleSingInClient : GoogleSignInClient? = null
    val RC_SIGN_IN = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        singup_button.setOnClickListener {
            createEmailId()
        }
        singin_button.setOnClickListener {
            SingInEmailId()
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSingInClient = GoogleSignIn.getClient(this,gso)

        google_button.setOnClickListener {
            var signInIntent = googleSingInClient?.signInIntent
            startActivityForResult(signInIntent,RC_SIGN_IN)
        }
    }

    fun createEmailId(){
        var email = email_edittext.text.toString()
        var password = password_edittext.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val text = "Molodec!"
                val duration = Toast.LENGTH_SHORT
                moveNextPage()
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
        }
    }

    fun SingInEmailId(){
        var email = email_edittext.text.toString()
        var password = password_edittext.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val text = "Molodec!"
                val duration = Toast.LENGTH_SHORT
                moveNextPage()
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
        }
    }

    fun firebaseAuthWithGoogle(acct : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(acct?.idToken,null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val text = "Molodec!"
                val duration = Toast.LENGTH_SHORT
                moveNextPage()
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            var account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        }
    }

    override fun onResume() {
        super.onResume()
        moveNextPage()
    }
    fun moveNextPage(){
        var currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            startActivity(Intent(this,Calendar_activity::class.java))
        }
    }
}