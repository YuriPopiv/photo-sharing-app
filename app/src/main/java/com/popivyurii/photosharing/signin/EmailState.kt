package com.popivyurii.photosharing.signin

import java.util.regex.Pattern

/**
 * Created by Yurii Popiv on 25.03.2022.
 */

// Consider an email valid if there's some text before and after a "@"
private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)\$"

class EmailState :
    TextFieldState(validator = ::isEmailValid, errorFor = ::emailValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun emailValidationError(email: String): String {
    return "Invalid email: $email"
}

private fun isEmailValid(email: String): Boolean {
    return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
}
