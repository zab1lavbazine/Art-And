package art.example.navigation.profileScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import art.example.ViewModel.UserViewModel
import art.example.navigation.BottomNavigationBar
import art.example.navigation.MyTopAppBar
import org.koin.androidx.compose.koinViewModel


@Composable
fun UserDetailedScreen(userId: Long, navController: NavHostController){

    val userViewModel: UserViewModel = koinViewModel()
    val selectedUser by userViewModel.selectedUser.observeAsState()
    val isLoading by userViewModel.isLoading.observeAsState(false)

    val selectedUserFolders by userViewModel.selectedFolder.observeAsState(null)
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    LaunchedEffect(userId) {
        userViewModel.getUserById(userId)
    }

    LaunchedEffect(selectedTabIndex.intValue) {
        if (selectedTabIndex.intValue == 1){
            userViewModel.getUserFoldersById(userId)
        }
    }


    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "User profile",
                showBackButton = true,
                onBackClicked = { navController.popBackStack() },
                onMoreClicked = { },
                onSearchClicked = {}
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ){

        }

    }

}