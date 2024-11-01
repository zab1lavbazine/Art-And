package art.example

import art.example.navigation.folderScreen.FolderDetailScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import art.example.navigation.folderScreen.CreateFolder
import art.example.navigation.postScreen.CreatePost
import art.example.navigation.startScreen.HelloScreen
import art.example.navigation.startScreen.LoginScreen
import art.example.navigation.profileScreen.MyProfile
import art.example.navigation.postScreen.PostDetailScreen
import art.example.navigation.postScreen.PostListScreen
import art.example.navigation.startScreen.RegisterScreen
import art.example.navigation.SearchScreen
import art.example.screen.Screen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyAppTheme {
                MyApp()
            }
        }
    }
}


@Composable
fun MyApp() {
    val navController = rememberNavController()

    // Login success callback
    val onLoginSuccess: (String) -> Unit = {
        navController.navigate(Screen.PostsScreen.route)
    }
    // register success callback to the login page
    val onRegisterSuccess: () -> Unit = {
        navController.navigate(Screen.Login.route)
    }


    NavHost(navController, startDestination = Screen.HelloScreen.route) {
        composable(Screen.RegisterScreen.route){
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = onRegisterSuccess
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                navController,
                onLoginSuccess = onLoginSuccess
            )
        }
        composable(Screen.HelloScreen.route) { HelloScreen(navController) }
        composable(Screen.PostsScreen.route) { PostListScreen(navController) }
        composable(Screen.MyProfile.route) { MyProfile(navController) }
        composable(Screen.CreatePost.route) { CreatePost(navController) }
        composable(Screen.CreateFolder.route) { CreateFolder(navController) }
        composable(Screen.SearchScreen.route) { SearchScreen(navController) }
        composable(
            Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.LongType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getLong("postId")
            if (postId != null) {
                PostDetailScreen(postId, navController = navController)
            }
        }

        composable(
            Screen.FolderDetail.route,
            arguments = listOf(navArgument("folderId") { type = NavType.LongType })
        ) { navBackStackEntry ->
            val folderId = navBackStackEntry.arguments?.getLong("folderId")
            if (folderId != null) {
                FolderDetailScreen(folderId, navController = navController)
            }
        }
    }
}

