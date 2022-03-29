package com.popivyurii.photosharing.signin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.popivyurii.photosharing.R
import com.popivyurii.photosharing.Screen
import com.popivyurii.photosharing.navigate
import com.popivyurii.photosharing.theme.SignInTheme

/**
 * Created by Yurii Popiv on 24.03.2022.
 */
class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels { SignInViewModelFactory() }

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.SignIn)
            }
        }

        auth = Firebase.auth


        return ComposeView(requireContext()).apply {
            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.sign_in_fragment

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setContent {
                SignInTheme{
                    SignInSignUp(
                        onEvent = {event ->
                            when (event) {
                                is SignInSignUpEvent.SignIn ->
                                    //viewModel.signIn(event.phone, event.password)
                                    signIn(event.phone, event.password)
                                    //startPhoneNumberVerification(event.phone)
                                else -> {

                                }
                            }},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            }

        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                        //if (task.exception?.message == "ERROR_USER_NOT_FOUND"){
                            createAccount(email, password)
//                        }else{
//                            Log.w(TAG, "signInWithEmail:failure", task.exception)
//                            Toast.makeText(requireContext(), "Authentication failed.",
//                                Toast.LENGTH_SHORT).show()
//                            updateUI(null)
//                        }
                }
            }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.let {
            navigate(Screen.Gallery, Screen.SignIn)
        }
    }

    private fun updateUI(user: FirebaseUser? = auth.currentUser) {
        user?.let {
            navigate(Screen.Gallery, Screen.SignIn)
            Log.d(TAG, "signInWithCredential:success")
            Log.d(TAG, user.toString())
        }

    }

    companion object {
        private const val TAG = "SignInFragment"
    }
}