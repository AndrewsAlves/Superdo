package com.andysapps.superdo.todo.activity.start_screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.activity.MainActivity
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.model.User
import com.andysapps.superdo.todo.views.IndeterminantProgressBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signin.btn_iv_showpass
import kotlinx.android.synthetic.main.activity_signin.btn_rl_google
import kotlinx.android.synthetic.main.activity_signin.btn_tv_signin
import kotlinx.android.synthetic.main.activity_signin.et_email
import kotlinx.android.synthetic.main.activity_signin.et_password
import kotlinx.android.synthetic.main.activity_signin.ll_google

class SignInActivity : AppCompatActivity() {

    public val TAG = "SignUpActivity"

    private lateinit var auth: FirebaseAuth

    val RC_SIGN_IN = 1

    var googleSignInClient : GoogleSignInClient? = null

    var progressBarSignup : IndeterminantProgressBar? = null
    var progressBarGoogle : IndeterminantProgressBar? = null

    var showpass = false

    var emailSignup = false
    var googelSignup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        initUi()
        updateUi()
    }

    fun initUi() {

        progressBarGoogle = progressBar_signin_google as IndeterminantProgressBar
        progressBarSignup = progressBar_signin as IndeterminantProgressBar

        progressBarGoogle!!.setColor(R.color.lightOrange)
        progressBarSignup!!.setColor(R.color.white)

        btn_iv_showpass.setOnClickListener {
            showpass = !showpass
            updateUi()
        }

        btn_rl_signin.setOnClickListener {
            emailSignup = true
            updateUi()
            signInUser()
        }

        btn_rl_google.setOnClickListener {
            googelSignup = true
            updateUi()
            signInUserGoogle()
        }

        btn_tv_signin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun updateUi() {
        if (showpass) {
            et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            et_password.setSelection(et_password.text.length)
            btn_iv_showpass.alpha = 1.0f
        } else {
            et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            et_password.setSelection(et_password.text.length)
            btn_iv_showpass.alpha = 0.2f
        }

        btn_tv_signin.visibility = View.VISIBLE
        progressBarSignup!!.visibility = View.INVISIBLE

        ll_google.visibility = View.VISIBLE
        progressBarGoogle!!.visibility = View.INVISIBLE

        if (emailSignup) {
            btn_tv_signin.visibility = View.INVISIBLE
            progressBarSignup!!.visibility = View.VISIBLE
        }

        if (googelSignup) {
            ll_google.visibility = View.INVISIBLE
            progressBarGoogle!!.visibility = View.VISIBLE
        }

        progressBarGoogle!!.setColor(R.color.lightOrange)
        progressBarSignup!!.setColor(R.color.white)
    }

    fun signInUser() {

        var email = et_email.text.toString()
        var password = et_password.text.toString()

        if (email == null) {
            email = " "
        }

        if (password == null) {
            password = " "
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        updateOrCreateUser()
                    }
                }.addOnFailureListener {

                    emailSignup = false
                    googelSignup = false
                    updateUi()

                    var ex = it as FirebaseAuthException

                    when (ex.errorCode) {
                        "ERROR_INVALID_CREDENTIAL" -> Toast.makeText(this, "Wrong email or password", Toast.LENGTH_LONG).show()
                        "ERROR_INVALID_EMAIL" -> {
                            et_email.error = "The email address is badly formatted."
                            et_email.requestFocus()
                         }
                         "ERROR_WRONG_PASSWORD" -> {
                             Toast.makeText(this, "Wrong password", Toast.LENGTH_LONG).show()
                         }
                        "ERROR_USER_DISABLED" -> Toast.makeText(this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show()
                        else -> {
                            toastError()
                        }
                    }

                    Log.d(TAG, "createUserWithEmail:failure " + ex.errorCode)
                }
    }

    fun signInUserGoogle() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun updateOrCreateUser() {
        var superdoUser = User()
        superdoUser.userId = auth.currentUser!!.uid
        superdoUser.email = auth.currentUser!!.email

        Log.e(FirestoreManager.TAG, "update or create user" + superdoUser.userId)

        FirebaseFirestore.getInstance().collection(FirestoreManager.DB_USER).document(superdoUser.userId)
                .set(superdoUser)
                .addOnSuccessListener {
                    Log.e(FirestoreManager.TAG, "DocumentSnapshot : Task uploadedAccount successfully written!")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e: Exception? ->
                    emailSignup = false
                    googelSignup = false
                    updateUi()
                    Log.e(FirestoreManager.TAG, "Error uploading task", e)
                    Toast.makeText(this, "Error sign up", Toast.LENGTH_LONG).show()
                }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                toastError()
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        updateOrCreateUser()
                    } else {
                        toastError()
                    }
                }
    }

    fun toastError() {
        Toast.makeText(this, "Error signing up", Toast.LENGTH_LONG).show()
    }
}
