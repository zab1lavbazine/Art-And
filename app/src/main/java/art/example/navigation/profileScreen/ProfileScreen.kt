package art.example.navigation.profileScreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import art.example.ViewModel.FolderViewModel
import art.example.ViewModel.PostViewModel
import art.example.ViewModel.TagViewModel
import art.example.ViewModel.UserViewModel
import art.example.api.data.Folder
import art.example.api.data.Post
import art.example.api.data.Tag
import art.example.api.data.User
import art.example.modules.AnalyticsLogger
import art.example.navigation.BottomNavigationBar
import art.example.navigation.GeneralMenuItem
import art.example.navigation.MenuItem
import art.example.navigation.MyTopAppBar
import art.example.navigation.postScreen.PostCard
import art.example.navigation.postScreen.TagBox
import art.example.navigation.postScreen.TagSelectionMenu
import art.example.navigation.supportElements.MyModalBottomSheet
import art.example.screen.FolderScreens
import art.example.screen.MiscScreens
import art.example.screen.PostScreens
import art.example.screen.Screen
import org.koin.androidx.compose.koinViewModel


@Composable
fun MyProfile(
    navController: NavHostController,
) {
    val userViewModel: UserViewModel = koinViewModel()
    val folderViewModel: FolderViewModel = koinViewModel()
    val postViewModel: PostViewModel = koinViewModel()
    val tagViewModel: TagViewModel = koinViewModel()


    val currentUser by userViewModel.currentUser.observeAsState()
    val isLoading by userViewModel.isLoading.observeAsState(false)
    val isFolderLoading by folderViewModel.isLoading.observeAsState(false)
    val isLoadingPost by postViewModel.isLoading.observeAsState(false)
//
    val userFolders by folderViewModel.folders.observeAsState()
    val userPosts by postViewModel.selectedPosts.observeAsState(emptyList())
    val tagList by tagViewModel.tags.observeAsState(emptyList())

    // State to manage the selected tab index
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    var showMoreClickDialog by remember { mutableStateOf(false) }
    var showUserEditDialog by remember { mutableStateOf(false) }

    val menuItems = mutableListOf(
        GeneralMenuItem(
            label = "Edit account info",
            onClickAction = { showUserEditDialog = true }
        ),
        GeneralMenuItem(
            label = "Log out",
            onClickAction = { userViewModel.logout { navController.navigate(MiscScreens.HelloScreen.route){ popUpTo(0)} } }
        )
    )


    // for getting current user from database
    LaunchedEffect(Unit) {
        AnalyticsLogger.logEvent("my_profile")
        Log.d("MyProfile", "Loading current user")
        userViewModel.getCurrentUser()
        postViewModel.getCurrentUserPosts()
        tagViewModel.loadTags()
    }


    LaunchedEffect(selectedTabIndex.intValue) {
        if (selectedTabIndex.intValue == 1){
            folderViewModel.getCurrentUserFolders()
        }
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "My Profile",
                showMoreClickedButton = true,
                onMoreClicked = { showMoreClickDialog = true }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        // Main container
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProfileHeader(isLoading, currentUser) }
            item { ProfileTabs(selectedTabIndex) }
            item { ProfileTabContent(selectedTabIndex, userPosts, userFolders, navController, isLoadingPost, isFolderLoading) }
        }

    }
    // open menu on More clicked button
    MyModalBottomSheet(
        showDialog = showMoreClickDialog,
        onDismissRequest = {
            showMoreClickDialog = false
        },
        menuItems = menuItems
    )

    if (showUserEditDialog){
        EditUserInfoDialog(
            user = currentUser,
            tagList = tagList,
            onDismiss = { showUserEditDialog = false},
            onConfirm = { updatedUser ->
                userViewModel.updateUserInfo(updatedUser)
                showUserEditDialog = false
            }
        )
    }
}


// profile header component
    @Composable
    fun ProfileHeader(isLoading: Boolean, currentUser: User?){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            if(isLoading){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                currentUser?.let { user ->
                    UserCard(user = user)
                } ?: run {
                    Text(text = "User not found", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }

    // for folder and post list tabs
    @Composable
    fun ProfileTabs(
        selectedTabIndex : MutableState<Int>
    ){
        TabRow(selectedTabIndex = selectedTabIndex.value) {
            Tab(
                selected = selectedTabIndex.value == 0,
                onClick = { selectedTabIndex.value = 0 },
                text = { Text("Posts") }
            )
            Tab(
                selected = selectedTabIndex.value == 1,
                onClick = { selectedTabIndex.value = 1 },
                text = { Text("Folders") }
            )
        }
    }


@Composable
fun ProfileTabContent(
    selectedTabIndex: MutableState<Int>,
    userPosts: List<Post>,
    userFolders: List<Folder>?,
    navController: NavHostController,
    isLoadingPost: Boolean,
    isFolderLoading: Boolean
) {
    when (selectedTabIndex.value) {
        0 -> UserPostsList(
            usersPosts = userPosts,
            navController = navController,
            isLoading = isLoadingPost
        )
        else -> UserFolderList(
            userFolders = userFolders,
            navController = navController,
            isFolderLoading = isFolderLoading
        )
    }
}




@Composable
fun EditUserInfoDialog(
    user: User?,
    tagList: List<Tag>,
    onDismiss: () -> Unit,
    onConfirm: (User) -> Unit
){
    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    val selectedTagIds = remember { mutableStateOf(user?.preferredTags?.map { it.id }?.toSet() ?: emptySet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Post") },
        text = {
            Column {
                TextField(
                    value = email                                                                                                         ,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") }
                )

                TagSelectionMenu(
                    tags = tagList,
                    selectedTags = selectedTagIds.value,
                    onTagSelected = { selectedTagIds.value = it },
                    onDismiss = {}
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    user?.copy(
                        email = email,
                        username = username,
                        preferredTags = selectedTagIds.value.map { id ->
                            tagList.find { it.id == id } ?: Tag(id, "Unknown")
                        }.toMutableList()
                    )?.let {
                        onConfirm(
                            it
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserCard(user: User) {
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

            user.preferredTags.let { tags ->
                if (tags.isNotEmpty()){
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Preferred Tags:", fontSize = 16.sp, color = Color.Black)
                    FlowRow(
                        modifier = Modifier.padding(top = 8.dp),
                        maxItemsInEachRow = 3,
                    ) { tags.forEach { tag ->
                        TagBox(tag = tag.name)
                    }

                    }
                }
            }
        }
    }
}

@Composable
fun UserFolderList(
    userFolders: List<Folder>?,
    navController: NavHostController,
    isFolderLoading: Boolean
){
    when {
        isFolderLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }
        }
        userFolders.isNullOrEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(text = "No Folders", fontSize = 20.sp, color = Color.Gray)
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                userFolders.forEach { folder ->
                    FolderCard(
                        folder = folder,
                        onClick = {
                            navController.navigate(FolderScreens.FolderDetail.createRoute(folder.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserPostsList(
    usersPosts: List<Post>?,
    navController: NavHostController,
    menuItems: List<MenuItem> = emptyList(),
    isLoading: Boolean
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }
        }
        usersPosts.isNullOrEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(text = "No Posts", fontSize = 20.sp, color = Color.Gray)
            }
        }
        else -> {
            // Use LazyColumn to display posts
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp) // Spacing between items
            ) {
                usersPosts.forEach { post ->
                    PostCard(
                        post = post,
                        onClick = { navController.navigate(PostScreens.PostDetail.createRoute(post.id)) },
                        menuItems = menuItems
                    )
                }
            }
        }
    }
}



@Composable
fun UserFoldersList(
    userFolders: List<Folder>,
    navController: NavHostController,
    isLoadingFolder: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Main content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                isLoadingFolder -> {
                    // Show loading indicator
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                userFolders.isEmpty() -> {
                    // Show "No Folders" message
                    Text(
                        text = "No Folders",
                        fontSize = 20.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    // Show folder list
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        userFolders.forEach { folder ->
                            FolderCard(
                                folder = folder,
                                onClick = {
                                    navController.navigate(FolderScreens.FolderDetail.createRoute(folderId = folder.id))
                                },
                            )
                        }

                    }
                }
            }
        }

        // Create Folder Button
        CreateFolderButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            onClick = {
                navController.navigate(FolderScreens.CreateFolder.route)
            }
        )
    }
}



@Composable
fun FolderCard(
    folder: Folder,
    onClick : () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = folder.title, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = folder.description, fontSize = 16.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CreateFolderButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit

) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
