package art.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import art.example.ViewModel.UserViewModel
import art.example.navigation.BottomNavigationBar
import art.example.navigation.LoginScreen
import art.example.navigation.MyBottomNavigationBar
import art.example.navigation.MyProfile
import art.example.navigation.MyTopAppBar
import art.example.navigation.PostDetailScreen
import art.example.navigation.PostListScreen
import art.example.screen.Screen
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApp()
        }
    }
}


@Composable
fun MyApp() {
    val navController = rememberNavController()

    // Login success callback
    val onLoginSuccess: (String) -> Unit = { token ->
        navController.navigate(Screen.PostsScreen.route)
    }

    NavHost(navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController, onLoginSuccess = onLoginSuccess) }
        composable(Screen.PostsScreen.route) { PostListScreen(navController) }
        composable(Screen.MyProfile.route) { MyProfile(navController) }
        composable(
            Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.LongType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getLong("postId")
            if (postId != null) {
                PostDetailScreen(postId, navController = navController)
            }
        }
    }
}

