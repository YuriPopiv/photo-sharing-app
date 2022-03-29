package com.popivyurii.photosharing.signin

import androidx.compose.runtime.Immutable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Created by Yurii Popiv on 24.03.2022.
 */
sealed class User {
    @Immutable
    data class LoggedInUser(val email: String) : User()
    object GuestUser : User()
    object NoUserLoggedIn : User()
}

object UserRepository {
    private lateinit var auth: FirebaseAuth



    fun signIn(phone: String, password: String) {
        auth = Firebase.auth
    }

}