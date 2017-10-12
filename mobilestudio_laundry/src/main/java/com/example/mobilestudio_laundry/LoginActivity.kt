package com.example.mobilestudio_laundry

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(),View.OnClickListener {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bt_signin.setOnClickListener(this)
        bt_signup.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        var currentUser : FirebaseUser? = mAuth?.getCurrentUser()
        updateUI(currentUser)
    }

    private fun createAccount(email : String, password : String){
        mAuth!!.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,OnCompleteListener<AuthResult>{ task ->
                        if(task.isSuccessful){
                            var user : FirebaseUser = mAuth?.currentUser!!
                            Toast.makeText(this,"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this,"회원가입이 실패했습니다. 이메일 형식이 아니거나 이미 존재하는 아이디입니다.",Toast.LENGTH_SHORT).show()
                        }
                })
    }

    private fun signIn(email: String,password: String){
        mAuth!!.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if(task.isSuccessful) {
                        var user : FirebaseUser = mAuth?.currentUser!!
                        updateUI(user)
                    } else {
                        Toast.makeText(this,"로그인에 실패했습니다. ID 혹은 PWD를 확인해주세요",Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                })
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            bt_signup.setVisibility(View.VISIBLE)
        }
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.bt_signin) {
            signIn(et_Email.text.toString(),et_passw.text.toString())
        } else if ( i == R.id.bt_signup) {
            createAccount(et_Email.text.toString(),et_passw.text.toString())
        }
    }
}
