package art.example.navigation.profileScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import art.example.ViewModel.PostViewModel
import art.example.ViewModel.UserViewModel
import art.example.api.data.User
import art.example.navigation.BottomNavigationBar
import art.example.navigation.MyTopAppBar
import org.koin.androidx.compose.koinViewModel


@Composable
fun UserDetailScreen(
    userId: Long,
    navController: NavHostController
){
    val userViewModel : UserViewModel = koinViewModel()
    val postViewModel : PostViewModel = koinViewModel()

    val isLoading by userViewModel.isLoading.observeAsState(false)
    val selectedUser by userViewModel.selectedUser.observeAsState()
    val selectedUsersPosts by postViewModel.selectedPosts.observeAsState()
    val errorMessage by userViewModel.errorMessage.observeAsState()

    LaunchedEffect(userId) {
        userViewModel.getSelectedUserById(userId)
        postViewModel.getUserPostsById(userId)
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "User Details",
                showMoreClickedButton = false,
                showBackButton = true,
                onBackClicked = { navController.popBackStack() },
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                UserProfileBox(selectedUser, isLoading)
            }
            item {
                UserPostsList(selectedUsersPosts, navController, menuItems = emptyList(), isLoading)
            }
        }
    }

}


@Composable
fun UserProfileBox(user: User?, isLoading: Boolean){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ){
        if (isLoading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        } else {
            user?.let { user ->
                UserCard(user)
            } ?: run {
                Text(text = "User not found", modifier = Modifier)
            }
        }
    }
}