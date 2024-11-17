package art.example

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import art.example.screen.Screen


@Composable
fun determineStartDestination(): String{
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getString("auth_token", null) != null

    return if (isLoggedIn) {
        Screen.PostsScreen.route
    } else {
        Screen.HelloScreen.route
    }
}