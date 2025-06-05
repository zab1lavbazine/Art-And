package art.example.navigation.startScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import art.example.ViewModel.UserViewModel
import art.example.navigation.MyTopAppBar
import art.example.screen.AuthScreens
import cz.fit.cvut.feature.translation.presentation.common.component.Translate
import cz.fit.cvut.feature.translation.presentation.common.component.t
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewPasswordScreen(
    navController: NavHostController
) {
    val userViewModel: UserViewModel = koinViewModel()

    val isLoading by userViewModel.isLoading.observeAsState(initial = false)
    val errorMessage by userViewModel.errorMessage.observeAsState(initial = null)

    var token by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordMatching by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            MyTopAppBar(title = t("New Password"))
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = t("Set a New Password"),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Reset Token Field
            TextField(
                value = token,
                onValueChange = { token = it },
                label = { t("Reset Token") },
                placeholder = { t("Enter your reset token") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Password Field
            TextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    isPasswordMatching = newPassword == confirmPassword // Update match status
                },
                label = { t("New Password") },
                placeholder = { t("Enter your new password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            TextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    isPasswordMatching = newPassword == confirmPassword // Update match status
                },
                label = { t("Confirm Password") },
                placeholder = { t("Re-enter your new password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = !isPasswordMatching
            )

            if (!isPasswordMatching) {
                Translate(
                    keyName = "Passwords do not match",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display error messages
            errorMessage?.let {
                if (it.isNotEmpty()) {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    userViewModel.sendNewPassword(token, newPassword, confirmPassword)
                    // navigate to the login page
                    navController.navigate(AuthScreens.Login.route)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = token.isNotEmpty() &&
                        newPassword.isNotEmpty() &&
                        isPasswordMatching &&
                        !isLoading
            ) {
                Text(text = if (isLoading) "Processing..." else "Submit")
            }
        }
    }
}
