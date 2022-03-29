package com.popivyurii.photosharing.signin

import android.telephony.PhoneNumberUtils

/**
 * Created by Yurii Popiv on 25.03.2022.
 */

class PhoneState :
    TextFieldState(validator = ::isPhoneValid, errorFor = ::phoneValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */
private fun phoneValidationError(phone: String): String {
    return "Invalid phone: $phone"
}

private fun isPhoneValid(phone: String): Boolean {
    return PhoneNumberUtils.isGlobalPhoneNumber(phone)
}