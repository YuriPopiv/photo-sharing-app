package com.popivyurii.photosharing.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.popivyurii.photosharing.Screen
import com.popivyurii.photosharing.Screen.Gallery
import com.popivyurii.photosharing.util.Event

/**
 * Created by Yurii Popiv on 24.03.2022.
 */
class SignInViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>>
        get() = _navigateTo

    fun signIn(phone: String, password: String) {
        userRepository.signIn(phone, password)
        _navigateTo.value = Event(Gallery)
    }

}

@Suppress("UNCHECKED_CAST")
class SignInViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(UserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}