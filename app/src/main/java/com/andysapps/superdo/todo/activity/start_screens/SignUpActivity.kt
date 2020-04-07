package com.andysapps.superdo.todo.activity.start_screens

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_sign_up)

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

        progressBarGoogle = progressBar_signup_google as IndeterminantProgressBar
        progressBarSignup = progressBar_signup as IndeterminantProgressBar

        progressBarGoogle!!.setColor(R.color.lightOrange)
        progressBarSignup!!.setColor(R.color.white)

        btn_iv_showpass.setOnClickListener {
            showpass = !showpass
            updateUi()
        }

        btn_rl_signup.setOnClickListener {
            emailSignup = true
            updateUi()
            signUpUser()
        }

        btn_rl_google.setOnClickListener {
            googelSignup = true
            updateUi()
            signUpUserGoogle()
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

        btn_tv_signup.visibility = View.VISIBLE
        progressBarSignup!!.visibility = View.INVISIBLE

        ll_google.visibility = View.VISIBLE
        progressBarGoogle!!.visibility = View.INVISIBLE

        if (emailSignup) {
            btn_tv_signup.visibility = View.INVISIBLE
            progressBarSignup!!.visibility = View.VISIBLE
        }

        if (googelSignup) {
            ll_google.visibility = View.INVISIBLE
            progressBarGoogle!!.visibility = View.VISIBLE
        }

        progressBarGoogle!!.setColor(R.color.lightOrange)
        progressBarSignup!!.setColor(R.color.white)
    }

    fun signUpUser() {

        val email = et_email.text.toString()
        val password = et_password.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        createNewUser()
                    }
                }.addOnFailureListener {

                    emailSignup = false
                    googelSignup = false
                    updateUi()

                    var ex = it as FirebaseAuthException

                    when (ex.errorCode) {
                        "ERROR_INVALID_CREDENTIAL" -> Toast.makeText(this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show()
                        "ERROR_INVALID_EMAIL" -> {
                            et_email.error = "The email address is badly formatted."
                            et_email.requestFocus()
                        }
                        "ERROR_EMAIL_ALREADY_IN_USE" -> {
                            et_email.error = "The email address is already in use by another account."
                            et_email.requestFocus()
                        }
                        "ERROR_CREDENTIAL_ALREADY_IN_USE" -> Toast.makeText(this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show()
                        "ERROR_USER_DISABLED" -> Toast.makeText(this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show()
                        "ERROR_WEAK_PASSWORD" -> {
                            et_password.error = "The password is invalid it must 6 characters at least"
                            et_password.requestFocus()
                        }
                        else -> {
                            toastError()
                        }
                    }

                    Log.d(TAG, "createUserWithEmail:failure " + ex.errorCode)
                }
    }

    fun signUpUserGoogle() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun createNewUser() {
        var superdoUser = User()
        superdoUser.userId = auth.currentUser!!.uid
        superdoUser.email = auth.currentUser!!.email

        FirebaseFirestore.getInstance().collection(FirestoreManager.DB_USER).document(superdoUser.userId)
                .set(superdoUser)
                .addOnSuccessListener(fun(it: Void) {
                    Log.d(FirestoreManager.TAG, "DocumentSnapshot : Task uploadedAccount successfully written!")
                    val intent = Intent(this, ProfileInfoActivity::class.java)
                    startActivity(intent)
                    finish()
                })
                .addOnFailureListener { e: Exception? ->
                    emailSignup = false
                    googelSignup = false
                    updateUi()
                    Log.e(FirestoreManager.TAG, "Error uploading task", e)
                    Toast.makeText(this, "Error sign up", Toast.LENGTH_LONG).show() }
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
                        createNewUser()

                        /*FirebaseFirestore.getInstance().collection(FirestoreManager.DB_USER).document(auth.currentUser!!.uid)
                                .get().addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                    }
                                }*/
                    } else {
                        toastError()
                    }
                }
    }

    fun toastError() {
        Toast.makeText(this, "Error signing up", Toast.LENGTH_LONG).show()
    }
}















/*
  switch (errorCode) {

                      case "ERROR_INVALID_CUSTOM_TOKEN":
                          Toast.makeText(MainActivity.this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_CUSTOM_TOKEN_MISMATCH":
                          Toast.makeText(MainActivity.this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_INVALID_CREDENTIAL":
                          Toast.makeText(MainActivity.this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_INVALID_EMAIL":
                          Toast.makeText(MainActivity.this, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                          etEmail.setError("The email address is badly formatted.");
                          etEmail.requestFocus();
                          break;

                      case "ERROR_WRONG_PASSWORD":
                          Toast.makeText(MainActivity.this, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                          etPassword.setError("password is incorrect ");
                          etPassword.requestFocus();
                          etPassword.setText("");
                          break;

                      case "ERROR_USER_MISMATCH":
                          Toast.makeText(MainActivity.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_REQUIRES_RECENT_LOGIN":
                          Toast.makeText(MainActivity.this, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                          Toast.makeText(MainActivity.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_EMAIL_ALREADY_IN_USE":
                          Toast.makeText(MainActivity.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                          etEmail.setError("The email address is already in use by another account.");
                          etEmail.requestFocus();
                          break;

                      case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                          Toast.makeText(MainActivity.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_USER_DISABLED":
                          Toast.makeText(MainActivity.this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_USER_TOKEN_EXPIRED":
                          Toast.makeText(MainActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_USER_NOT_FOUND":
                          Toast.makeText(MainActivity.this, "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_INVALID_USER_TOKEN":
                          Toast.makeText(MainActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_OPERATION_NOT_ALLOWED":
                          Toast.makeText(MainActivity.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                          break;

                      case "ERROR_WEAK_PASSWORD":
                          Toast.makeText(MainActivity.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                          etPassword.setError("The password is invalid it must 6 characters at least");
                          etPassword.requestFocus();
                          break;

                  }
   */
