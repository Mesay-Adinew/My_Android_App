package com.example.volleyvolleyregistrationsystem

//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var id: TextView
    internal lateinit var userName: TextView
    internal lateinit var userEmail: TextView
    internal lateinit var gender: TextView
    internal lateinit var btnLogout: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            id = findViewById(R.id.textViewId)
            userName = findViewById(R.id.textViewUsername)
            userEmail = findViewById(R.id.textViewEmail)
            gender = findViewById(R.id.textViewGender)
            btnLogout = findViewById(R.id.buttonLogout)

            val user = SharedPrefManager.getInstance(this).user

            id.text = user.id.toString()
            userEmail.text = user.email
            gender.text = user.gender
            userName.text = user.name

            btnLogout.setOnClickListener(this)

        } else {
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onClick(view: View) {
        if (view == btnLogout) {
            SharedPrefManager.getInstance(applicationContext).logout()
        }
    }
}