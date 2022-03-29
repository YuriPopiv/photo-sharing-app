package com.popivyurii.photosharing.signin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.popivyurii.photosharing.R

/**
 * Created by Yurii Popiv on 25.03.2022.
 */
sealed class SignInSignUpEvent {
    data class SignIn(val phone: String, val password: String) : SignInSignUpEvent()
    object SignUp : SignInSignUpEvent()
    object SignInAsGuest : SignInSignUpEvent()
    object NavigateBack : SignInSignUpEvent()
}

@Composable
fun SignInSignUp(
    onEvent: (SignInSignUpEvent) -> Unit,
    modifier: Modifier = Modifier
){
    val phoneState = remember { EmailState() }
    var brandingBottom by remember { mutableStateOf(0f) }
    val focusRequester = remember { FocusRequester() }
    Surface(color = Color.White) {

        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Branding(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned {
                        if (brandingBottom == 0f) {
                            brandingBottom = it.boundsInParent().bottom
                        }
                    }
            )

//        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
//            Text(
//                text = stringResource(id = R.string.sign_in_create_account),
//                style = MaterialTheme.typography.subtitle2,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.padding(top = 64.dp, bottom = 12.dp)
//            )
//        }
//        val onSubmit = {
//            if (phoneState.isValid) {
//                onEvent(SignInSignUpEvent.SignIn(phoneState.text))
//            } else {
//                phoneState.enableShowErrors()
//            }
//        }
            //Phone(phoneState = phoneState, imeAction = ImeAction.Done, onImeAction = onSubmit)
            Phone(phoneState = phoneState, imeAction = ImeAction.Done, onImeAction = { focusRequester.requestFocus() })

            Spacer(modifier = Modifier.height(16.dp))

            val passwordState = remember { PasswordState() }
            Password(
                label = stringResource(id = R.string.password),
                passwordState = passwordState,
                modifier = Modifier.focusRequester(focusRequester),
                onImeAction = { onEvent(SignInSignUpEvent.SignIn(phoneState.text, passwordState.text)) }
            )

            Button(
                //onClick = onSubmit,
                onClick = { onEvent(SignInSignUpEvent.SignIn(phoneState.text, passwordState.text)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 3.dp),
                enabled = phoneState.isValid && passwordState.isValid
            ) {
                Text(
                    text = stringResource(id = R.string.user_signin),
                    style = MaterialTheme.typography.subtitle2
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
//        Logo(
//            modifier = Modifier
//                .align(Alignment.CenterHorizontally)
//                .padding(horizontal = 76.dp)
//        )
        Text(
            text = stringResource(id = R.string.app_title),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun Phone(
    phoneState: TextFieldState = remember { EmailState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = phoneState.text,
        onValueChange = {
            phoneState.text = it
        },
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = R.string.phone),
                    style = MaterialTheme.typography.body2
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                phoneState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    phoneState.enableShowErrors()
                }
            },
        textStyle = MaterialTheme.typography.body2,
        isError = phoneState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

    phoneState.getError()?.let { error -> TextFieldError(textError = error) }
}

@Composable
fun Password(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val showPassword = remember { mutableStateOf(false) }
    OutlinedTextField(
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
            passwordState.enableShowErrors()
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                passwordState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    passwordState.enableShowErrors()
                }
            },
        textStyle = MaterialTheme.typography.body2,
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2
                )
            }
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = stringResource(id = R.string.hide_password)
                    )
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(id = R.string.show_password)
                    )
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        isError = passwordState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

    passwordState.getError()?.let { error -> TextFieldError(textError = error) }
}

@Composable
fun TextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
        )
    }
}