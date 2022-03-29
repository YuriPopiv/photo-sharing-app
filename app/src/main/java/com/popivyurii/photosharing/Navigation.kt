package com.popivyurii.photosharing

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.security.InvalidParameterException

/**
 * Created by Yurii Popiv on 24.03.2022.
 */
enum class Screen { SignIn, Gallery }
fun Fragment.navigate(to: Screen, from: Screen) {
    if (to == from) {
        throw InvalidParameterException("Can't navigate to $to")
    }
    when (to) {
        Screen.SignIn -> {
            findNavController().navigate(R.id.sign_in_fragment)
        }

        Screen.Gallery -> {
            findNavController().navigate(R.id.gallery_fragment)
        }
    }
}