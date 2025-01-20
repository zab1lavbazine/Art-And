package art.example.navigation.startScreen

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Make sure to import this
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import art.example.ViewModel.UserViewModel
import art.example.screen.AuthScreens
import art.example.screen.PostScreens
import art.example.screen.Screen
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
@Composable
fun LoginScreen(
    navController: NavHostController,
) {
    val userViewModel: UserViewModel = koinViewModel()

    val savedUser = userViewModel.getSavedUser()

    var username by remember { mutableStateOf(savedUser?.username ?: "") }
    var password by remember { mutableStateOf(savedUser?.password ?: "") } // Default to empty or the saved password
    val isLoading by userViewModel.isLoading.observeAsState(initial = false)
    // Observe error message from ViewModel
    val errorMessage by userViewModel.errorMessage.observeAsState()



    // automatic login to the application
    LaunchedEffect(savedUser) {
        if (savedUser != null) {
            userViewModel.login(savedUser.username, savedUser.password) { token ->
                navController.navigate(PostScreens.PostsScreen.route) {
                    popUpTo(AuthScreens.Login.route) { inclusive = true }
                }
            }
        }
    }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            TextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
                placeholder = { Text("Enter your username") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            // Display error message if there's one
            errorMessage?.let {
                if (it.isNotEmpty()) {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                userViewModel.login(username, password) { token ->
                    navController.navigate(PostScreens.PostsScreen.route) // Navigate to PostsScreen on successful login
                }
            }) {
                Text(text = if (isLoading) "Logging in..." else "Login")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate(AuthScreens.ResetPassword.route)
            },
                modifier = Modifier
                    .height(48.dp)
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                )
            ){
                Text("Reset password")
            }
        }
}
