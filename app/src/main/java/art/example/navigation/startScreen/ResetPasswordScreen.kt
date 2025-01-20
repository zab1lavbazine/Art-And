package art.example.navigation.startScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import art.example.ViewModel.UserViewModel
import art.example.navigation.MyTopAppBar
import art.example.screen.AuthScreens
import org.koin.androidx.compose.koinViewModel
import java.util.regex.Pattern

@Composable
fun ResetPasswordScreen(
    navHostController: NavHostController
){

    val userViewModel: UserViewModel = koinViewModel()

    val isLoading by userViewModel.isLoading.observeAsState(initial = false)
    val errorMessage by userViewModel.errorMessage.observeAsState(initial = null)
    val apiResponse by userViewModel.apiResponse.observeAsState(initial = null)


    var email by remember { mutableStateOf(TextFieldValue("")) }
    var isEmailValid by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Reset Password",
                showBackButton = true,
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
            ,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Email Input Field
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = isValidEmail(it.text)
                                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email Address") },
                placeholder = { Text("Enter your email") },
                singleLine = true
            )

            if (!isEmailValid) {
                Text(
                    text = "Please enter a valid email address.",
                    color = Color.Red,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            errorMessage?.let {
                if (it.isNotEmpty()) {
                    Text(
                        text = it,
                        color = Color.Green,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Spacer
            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    userViewModel.resetPassword(email.text)
                    navHostController.navigate(AuthScreens.NewPassword.route)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(text = if (isLoading) "Sending Request..." else "Reset Password")
            }
        }
    }

}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Pattern.compile(
        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    )
    return emailRegex.matcher(email).matches()
}