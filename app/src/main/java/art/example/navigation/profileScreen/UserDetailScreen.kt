package art.example.navigation.profileScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import art.example.ViewModel.PostViewModel
import art.example.ViewModel.UserViewModel
import art.example.api.data.SelectedUser
import art.example.api.data.User
import art.example.modules.AnalyticsLogger
import art.example.navigation.BottomNavigationBar
import art.example.navigation.MyTopAppBar
import art.example.navigation.postScreen.TagBox
import org.koin.androidx.compose.koinViewModel


@Composable
fun UserDetailScreen(
    userId: Long,
    navController: NavHostController
){
    val userViewModel : UserViewModel = koinViewModel()

    val isLoading by userViewModel.isLoading.observeAsState(false)
    val selectedUser by userViewModel.selectedUser.observeAsState()
    val errorMessage by userViewModel.errorMessage.observeAsState()

    LaunchedEffect(userId) {
        userViewModel.getSelectedUserById(userId)
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
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                UserProfileBox(selectedUser, isLoading)
            }
            item {
                UserPostsList(selectedUser?.posts, navController, menuItems = emptyList(), isLoading)
            }
        }
    }

}


@Composable
fun UserProfileBox(user: SelectedUser?, isLoading: Boolean){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                SelectedUserCard(user)
            } ?: run {
                Text(text = "User not found", modifier = Modifier)
            }
        }
    }
}


@Composable
fun SelectedUserCard(user: SelectedUser){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.username, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.email, fontSize = 16.sp, color = Color.Gray)
        }
    }
}