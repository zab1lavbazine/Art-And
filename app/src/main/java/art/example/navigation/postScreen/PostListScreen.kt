package art.example.navigation.postScreen


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import art.example.ViewModel.PostViewModel
import art.example.api.data.Post
import art.example.navigation.BottomNavigationBar
import art.example.navigation.MenuItem
import art.example.navigation.MyTopAppBar
import art.example.navigation.supportElements.Placeholder
import art.example.navigation.supportElements.ResolvePostImage
import art.example.screen.MiscScreens
import art.example.screen.PostScreens
import art.example.screen.Screen
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostListScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: PostViewModel = koinViewModel()
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val isLoading by viewModel.isLoading.observeAsState(false)


    val gridState = rememberLazyGridState()

    // triggering refresh of the page
    val pullRefreshState = rememberPullRefreshState(
        refreshing = posts.loadState.refresh is LoadState.Loading,
        onRefresh = {
            posts.refresh()
        }
    )



    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Posts",
                showBackButton = false,
                showSearchButton = true,
                onSearchClicked = { navController.navigate(MiscScreens.SearchScreen.route) },
                onMoreClicked = { /* Handle more options */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        // Add padding to account for top and bottom bars
        // Display a grid layout of posts

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState), // Attach the pull refresh state here
            contentAlignment = Alignment.Center
        ) {
            // Show loading indicator when refreshing
            when {
                posts.loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Blue
                    )
                }
                posts.loadState.refresh is LoadState.Error -> {
                    val error = (posts.loadState.refresh as LoadState.Error).error
                    Text(
                        text = "Error loading posts: ${error.localizedMessage}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                posts.loadState.append is LoadState.Error -> {
                    val error = (posts.loadState.append as LoadState.Error).error
                    Text(
                        text = "Error loading more posts: ${error.localizedMessage}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                posts.itemSnapshotList.isEmpty() -> {
                    // Empty state: show a message when there are no posts
                    Text(
                        text = "No posts available",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    PostGrid(
                        posts = posts,
                        onClick = { postId -> navController.navigate(PostScreens.PostDetail.createRoute(postId)) },
                        gridState = gridState
                    )
                }
            }
        }
    }

}



@Composable
fun PostGrid(
    posts: LazyPagingItems<Post>,
    onClick: (Long) -> Unit,
    gridState: LazyGridState
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        state = gridState // Use the saved LazyListState here
    ) {
        items(count = posts.itemCount) { index ->
            val post = posts[index]
            if (post != null) {
                PostCard(
                    post = post,
                    onClick = { onClick(post.id) }
                )
            }
        }

        posts.apply {
            when {
                loadState.append is LoadState.Loading -> {
                    item { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) }
                }
                loadState.append is LoadState.Error -> {
                    val error = loadState.append as LoadState.Error
                    item {
                        Text(text = "Error: ${error.error.message}", color = Color.Red)
                    }
                }
            }
        }
    }

    // Listen for when the end of the list is reached to trigger the retry
    LaunchedEffect(posts.loadState.append) {
        if (posts.loadState.append is LoadState.NotLoading && posts.itemCount > 0) {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
            val isNearEnd = lastVisibleItem != null && lastVisibleItem.index >= posts.itemCount - 5
            if (isNearEnd) {
                posts.retry()
            }
        }
    }
}




@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    menuItems: List<MenuItem> = emptyList()
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var expanded by remember { mutableStateOf(false) }

    val maxCardHeight = 200.dp // Set desired max height

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .heightIn(max = maxCardHeight)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            // display post image in the box
            ResolvePostImage(post, maxCardHeight)

            if (menuItems.isNotEmpty()) {
                // More options button
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
            }


            // show only it is not empty
            // Dropdown menu with options
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                menuItems.forEach { menuItem ->
                    DropdownMenuItem(
                        text = { Text(menuItem.label) },
                        onClick = {
                            menuItem.onClick()
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

