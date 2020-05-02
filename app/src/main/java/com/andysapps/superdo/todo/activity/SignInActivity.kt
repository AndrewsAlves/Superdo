package com.andysapps.superdo.todo.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.events.CreateOrUpdateUserFailureEvent
import com.andysapps.superdo.todo.events.CreateOrUpdateUserSuccessEvent
import com.andysapps.superdo.todo.events.FetchUserFailureEvent
import com.andysapps.superdo.todo.events.FetchUserSuccessEvent
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
import kotlinx.android.synthetic.main.activity_signin.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SignInActivity : AppCompatActivity() {

    val TAG = "SignUpActivity"

    var pressedBack = false

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
        EventBus.getDefault().register(this)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        initUi()
        updateUi()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!pressedBack) {
            pressedBack = true
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_LONG).show()
            var handler = Handler()
            handler.postDelayed(Runnable {  pressedBack = false}, 3000)
            return
        }
        super.onBackPressed()
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

        btn_tv_forgotpassword.setOnClickListener {

        }

        btn_tv_signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
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

    fun stopLoading() {
        emailSignup = false
        googelSignup = false
        updateUi()
    }

    fun signInUser() {

        if (!Utils.isNetworkConnected(this)) {
            stopLoading()
            return
        }

        var email = et_email.text.toString()
        var password = et_password.text.toString()

        if (email.trim().isEmpty()) {
            et_email.error = "Enter Email id"
            et_email.requestFocus()
            return
        }
        if (password.trim().isEmpty()) {
            et_email.error = "Enter password"
            et_email.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        FirestoreManager.getInstance().fetchUser(null, false)
                    }
                }.addOnFailureListener {
                    stopLoading()
                    var ex = it as FirebaseAuthException

                    when (ex.errorCode) {
                        "ERROR_INVALID_CREDENTIAL" -> Toast.makeText(this, "Wrong email or password", Toast.LENGTH_LONG).show()
                        "ERROR_INVALID_EMAIL" -> {
                            et_email.error = "The email address is badly formatted."
                            et_email.requestFocus()
                         }
                         "ERROR_WRONG_PASSWORD" -> {
                             et_password.error = "Wrong password."
                             et_password.requestFocus()
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
        if (!Utils.isNetworkConnected(this)) {
            stopLoading()
            return
        }
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun updateOrCreateUser() {
        var superdoUser = User()
        superdoUser.userId = auth.currentUser!!.uid
        superdoUser.email = auth.currentUser!!.email

        if (auth.currentUser!!.displayName != null) {
            var fullName = auth.currentUser!!.displayName
            Log.e(TAG, "Display name : $fullName")
            var names = fullName!!.split(' ')
            superdoUser.firstName = names[0]
            if (names.size >= 2) {
                superdoUser.lastName = names[1]
            }
        }

        FirestoreManager.getInstance().createOrUpdateUser(superdoUser)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                googleSignInClient!!.signOut()
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
                        FirestoreManager.getInstance().fetchUser(null, false)
                    } else {
                        toastError()
                    }
                }
    }

    fun toastError() {
        Toast.makeText(this, "Error signing up", Toast.LENGTH_LONG).show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event : CreateOrUpdateUserSuccessEvent) {
        FirestoreManager.getInstance().updateBucket(FirestoreManager.getInstance().defaultPersonalbucket)
        val intent = Intent(this, ProfileInfoActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event : CreateOrUpdateUserFailureEvent) {
        stopLoading()
        toastError()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FetchUserSuccessEvent?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FetchUserFailureEvent?) {
        updateOrCreateUser()
    }
}
