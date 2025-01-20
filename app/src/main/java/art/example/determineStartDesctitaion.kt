package art.example

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import art.example.ViewModel.UserViewModel
import art.example.screen.MiscScreens
import art.example.screen.PostScreens


fun determineStartDestination(
    userViewModel: UserViewModel,
    onStartDestinationDetermined: (String) -> Unit
) {
    val savedUser = userViewModel.getSavedUser()

    if (savedUser != null && savedUser.username.isNotEmpty() && savedUser.password.isNotEmpty()) {
        userViewModel.login(savedUser.username, savedUser.password) { token ->

            if (token.isNotEmpty()) {
                onStartDestinationDetermined(PostScreens.PostsScreen.route)  // Update the start destination after login
            } else {
                Log.d("FLOW", "ERROR LOGGING IN AUTO")
                onStartDestinationDetermined(MiscScreens.HelloScreen.route)  // No user credentials, navigate to HelloScreen
            }
        }
    } else {
        Log.d("FLOW", "No saved credentials, navigating to HelloScreen")
        onStartDestinationDetermined(MiscScreens.HelloScreen.route)  // If no credentials, navigate to HelloScreen
    }
}



@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Optional background color
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.primary,
            strokeWidth = 4.dp
        )
    }
}

