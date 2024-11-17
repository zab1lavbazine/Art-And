package art.example.navigation.startScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import art.example.ViewModel.UserViewModel
import art.example.screen.Screen
import org.koin.androidx.compose.koinViewModel

// Define the MainScreen composable
@Composable
fun HelloScreen(navController: NavController) {


    val userViewModel: UserViewModel = koinViewModel()
    val savedUser = userViewModel.getSavedUser()

    LaunchedEffect(savedUser){
        if (savedUser != null) {
            if (savedUser.username.isNotEmpty() && savedUser.password.isNotEmpty()){
                userViewModel.login(savedUser.username, savedUser.password) {
                    token -> if (token.isNotEmpty()){
                        navController.navigate(Screen.PostsScreen.route){
                            popUpTo(Screen.HelloScreen.route) { inclusive = true}
                        }
                }
                }

            }
        }

    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to ArtHub",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { navController.navigate(Screen.Login.route) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Go to Login Page")
        }

        Button(
            onClick = { navController.navigate(Screen.RegisterScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Register Page")
        }
    }
}
