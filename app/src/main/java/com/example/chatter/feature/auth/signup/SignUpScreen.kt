package com.example.chatter.feature.auth.signup

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chatter.ui.animation.fadeInEffect
import com.example.chatter.ui.animation.pulseEffect
import com.example.chatter.ui.animation.scaleInEffect
import com.example.chatter.ui.animation.slideInFromLeftEffect

data class SignUpFormState(
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
)

data class PasswordVisibilityState(
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

interface SignUpFormCallbacks {
    fun onEmailChange(email: String)
    fun onPasswordChange(password: String)
    fun onConfirmChange(confirm: String)
    fun onPasswordVisibilityChange()
    fun onConfirmVisibilityChange()
    fun onSignUp()
    fun onSignIn()
}

@Composable
private fun SignUpHeader() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fadeInEffect(visible = visible)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .scaleInEffect(visible = visible)
                .pulseEffect(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PersonAdd,
                contentDescription = "Sign Up Logo",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Join Chatter",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.slideInFromLeftEffect(visible = visible, delay = 100)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start your messaging journey",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.slideInFromLeftEffect(visible = visible, delay = 200)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text("Email") },
        singleLine = true,
        isError = emailError != null,
        supportingText = {
            if (emailError != null) {
                Text(text = emailError, color = MaterialTheme.colorScheme.error)
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email"
            )
        }
    )
}

@Composable
private fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Password"
            )
        },
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (isVisible) "Hide password" else "Show password"
                )
            }
        }
    )
}

@Composable
private fun SignUpButtons(
    isLoading: Boolean,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onSignUpClick,
            enabled = !isLoading && isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sign Up")
            }
        }

        TextButton(
            onClick = onSignInClick,
            enabled = !isLoading
        ) {
            Text("Already have an account? Sign In")
        }
    }
}

@Composable
private fun SignUpContent(
    formState: SignUpFormState,
    visibilityState: PasswordVisibilityState,
    callbacks: SignUpFormCallbacks,
    isLoading: Boolean
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .imePadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        SignUpHeader()

        EmailField(
            email = formState.email,
            onEmailChange = callbacks::onEmailChange,
            emailError = formState.emailError,
            modifier = Modifier
                .fadeInEffect(visible = visible, delay = 300)
                .slideInFromLeftEffect(visible = visible, delay = 300)
        )

        PasswordField(
            password = formState.password,
            onPasswordChange = callbacks::onPasswordChange,
            label = "Password",
            isVisible = visibilityState.isPasswordVisible,
            onVisibilityChange = callbacks::onPasswordVisibilityChange,
            error = formState.passwordError,
            modifier = Modifier
                .fadeInEffect(visible = visible, delay = 400)
                .slideInFromLeftEffect(visible = visible, delay = 400)
        )

        PasswordField(
            password = formState.confirm,
            onPasswordChange = callbacks::onConfirmChange,
            label = "Confirm Password",
            isVisible = visibilityState.isConfirmPasswordVisible,
            onVisibilityChange = callbacks::onConfirmVisibilityChange,
            error = if (formState.confirm.isNotEmpty() && formState.password != formState.confirm)
                "Passwords do not match" else null,
            modifier = Modifier
                .fadeInEffect(visible = visible, delay = 500)
                .slideInFromLeftEffect(visible = visible, delay = 500)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SignUpButtons(
            isLoading = isLoading,
            onSignUpClick = callbacks::onSignUp,
            onSignInClick = callbacks::onSignIn,
            isEnabled = formState.email.isNotEmpty() &&
                    formState.password.isNotEmpty() &&
                    formState.confirm.isNotEmpty() &&
                    formState.password == formState.confirm,
            modifier = Modifier
                .fadeInEffect(visible = visible, delay = 600)
                .slideInFromLeftEffect(visible = visible, delay = 600)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SignUpScreen(navController: NavController) {
    val viewModel: SignUpViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()

    var formState by remember { mutableStateOf(SignUpFormState()) }
    var visibilityState by remember { mutableStateOf(PasswordVisibilityState()) }

    val validateEmail: (String) -> String? = { input ->
        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches() && input.isNotEmpty()) {
            "Please enter a valid email"
        } else null
    }

    val validatePassword: (String) -> String? = { input ->
        if (input.length < 6 && input.isNotEmpty()) {
            "Password must be at least 6 characters"
        } else null
    }

    val callbacks = object : SignUpFormCallbacks {
        override fun onEmailChange(email: String) {
            formState = formState.copy(
                email = email,
                emailError = validateEmail(email)
            )
        }

        override fun onPasswordChange(password: String) {
            formState = formState.copy(
                password = password,
                passwordError = validatePassword(password)
            )
        }

        override fun onConfirmChange(confirm: String) {
            formState = formState.copy(confirm = confirm)
        }

        override fun onPasswordVisibilityChange() {
            visibilityState = visibilityState.copy(
                isPasswordVisible = !visibilityState.isPasswordVisible
            )
        }

        override fun onConfirmVisibilityChange() {
            visibilityState = visibilityState.copy(
                isConfirmPasswordVisible = !visibilityState.isConfirmPasswordVisible
            )
        }

        override fun onSignUp() {
            viewModel.signUp(formState.email.trim(), formState.password.trim())
        }

        override fun onSignIn() {
            navController.popBackStack()
        }
    }

    val context = LocalContext.current
    LaunchedEffect(uiState.value) {
        when (uiState.value) {
            is SignUpState.Success -> {
                navController.navigate("conversation_screen") {
                    popUpTo("signup") { inclusive = true }
                }
            }

            is SignUpState.Error -> {
                Toast.makeText(
                    context,
                    "Failed to create account. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            SignUpState.Loading,
            SignUpState.Nothing -> { /* State handled by UI */
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            SignUpContent(
                formState = formState,
                visibilityState = visibilityState,
                callbacks = callbacks,
                isLoading = uiState.value == SignUpState.Loading
            )
        }
    }
}