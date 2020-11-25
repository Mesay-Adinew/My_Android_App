package com.example.volleyvolleyregistrationsystem
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_registeration.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class Registeration : AppCompatActivity() {
    internal lateinit var editTextUsername: EditText
    internal lateinit var editTextEmail: EditText
    internal lateinit var editTextPassword: EditText
    internal lateinit var radioGroupGender: RadioGroup
    internal lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeration)
        progressBar = findViewById(R.id.progressBar)

        //if the user is already logged in we will directly start the MainActivity (profile) activity
        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
            return
        }

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        radioGroupGender = findViewById(R.id.radioGender)

        buttonRegister.setOnClickListener(View.OnClickListener {
            //if user pressed on button register
            //here we will register the user to server
            registerUser()
        })

        textViewLogin.setOnClickListener(View.OnClickListener {
            finish()
            startActivity(Intent(this@Registeration, Login::class.java))
        })
    }

    private fun registerUser() {
        val username = editTextUsername.text.toString().trim { it <= ' ' }
        val email = editTextEmail.text.toString().trim { it <= ' ' }
        val password = editTextPassword.text.toString().trim { it <= ' ' }

        val gender = (findViewById<View>(radioGroupGender.checkedRadioButtonId) as RadioButton).text.toString()

        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            editTextUsername.error = "Please enter username"
            editTextUsername.requestFocus()
            return
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.error = "Please enter your email"
            editTextEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Enter a valid email"
            editTextEmail.requestFocus()
            return
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.error = "Enter a password"
            editTextPassword.requestFocus()
            return
        }

        val stringRequest = object : StringRequest(
            Method.POST, URLs.URL_REGISTER,
            Response.Listener { response ->
                progressBar.visibility = View.GONE

                try {
                    //converting response to json object
                    val obj = JSONObject(response)
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show()

                        //getting the user from the response
                        val userJson = obj.getJSONObject("user")

                        //creating a new user object
                        val user = User(
                            userJson.getInt("id"),
                            userJson.getString("username"),
                            userJson.getString("email"),
                            userJson.getString("gender")
                        )

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(applicationContext).userLogin(user)

                        //starting the MainActivity activity
                        finish()
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    } else {
                        Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                params["email"] = email
                params["password"] = password
                params["gender"] = gender
                return params
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
}