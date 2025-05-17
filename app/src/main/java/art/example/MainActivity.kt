package art.example

import art.example.navigation.folderScreen.FolderDetailScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import art.example.ViewModel.UserViewModel
import art.example.navigation.folderScreen.CreateFolder
import art.example.navigation.postScreen.CreatePost
import art.example.navigation.startScreen.HelloScreen
import art.example.navigation.startScreen.LoginScreen
import art.example.navigation.postScreen.PostDetailScreen
import art.example.navigation.postScreen.PostListScreen
import art.example.navigation.startScreen.RegisterScreen
import art.example.navigation.SearchScreen
import art.example.navigation.profileScreen.MyProfile
import art.example.navigation.profileScreen.UserDetailScreen
import art.example.navigation.startScreen.NewPasswordScreen
import art.example.navigation.startScreen.ResetPasswordScreen
import art.example.screen.AuthScreens
import art.example.screen.FolderScreens
import art.example.screen.MiscScreens
import art.example.screen.PostScreens
import art.example.screen.Screen
import art.example.screen.UserScreens
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import org.koin.androidx.compose.koinViewModel

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
    val userViewModel: UserViewModel = koinViewModel()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userViewModel) {
        determineStartDestination(
            userViewModel = userViewModel,
            onStartDestinationDetermined = { destination ->
                startDestination = destination
            }
        )
    }

    if (startDestination == null) {
        LoadingScreen()
    } else {
        NavHost(navController, startDestination = startDestination!!) {
            composable(AuthScreens.Register.route) {
                RegisterScreen(
                    navController = navController,
                    onRegisterSuccess = {
                        navController.navigate(AuthScreens.Login.route) {
                            popUpTo(MiscScreens.HelloScreen.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AuthScreens.Login.route) {
                LoginScreen(navController)
            }

            composable(AuthScreens.ResetPassword.route) {
                ResetPasswordScreen(navController)
            }

            composable(AuthScreens.NewPassword.route) {
                NewPasswordScreen(navController)
            }

            composable(MiscScreens.HelloScreen.route) {
                HelloScreen(navController)
            }

            composable(PostScreens.PostsScreen.route) {
                PostListScreen(navController)
            }

            composable(UserScreens.MyProfile.route) {
                MyProfile(navController)
            }

            composable(PostScreens.CreatePost.route) {
                CreatePost(navController)
            }

            composable(FolderScreens.CreateFolder.route) {
                CreateFolder(navController)
            }

            composable(MiscScreens.SearchScreen.route) {
                SearchScreen(navController)
            }

            composable(
                PostScreens.PostDetail.route,
                arguments = listOf(navArgument("postId") { type = NavType.LongType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getLong("postId")
                if (postId != null) {
                    PostDetailScreen(postId, navController = navController)
                }
            }

            composable(
                UserScreens.UserDetail.route,
                arguments = listOf(navArgument("userId") { type = NavType.LongType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getLong("userId")
                if (userId != null) {
                    UserDetailScreen(userId, navController = navController)
                }
            }

            composable(
                FolderScreens.FolderDetail.route,
                arguments = listOf(navArgument("folderId") { type = NavType.LongType })
            ) { navBackStackEntry ->
                val folderId = navBackStackEntry.arguments?.getLong("folderId")
                if (folderId != null) {
                    FolderDetailScreen(folderId, navController = navController)
                }
            }
        }
    }
}

