package art.example.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import art.example.ViewModel.PostViewModel
import art.example.navigation.postScreen.PostGrid
import art.example.screen.PostScreens
import art.example.screen.Screen
import cz.fit.cvut.feature.translation.presentation.common.component.t
import org.koin.androidx.compose.koinViewModel


@Composable
fun SearchScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: PostViewModel = koinViewModel()
    var searchText by remember { mutableStateOf("") }
    var triggerSearch by remember { mutableStateOf(false) }

    // Collect search results based on the query
    val searchFlow = remember(searchText) {
        derivedStateOf { viewModel.searchPosts(searchText) }
    }.value

    val searchResults = searchFlow.collectAsLazyPagingItems()

    // Remember LazyGridState
    val gridState = rememberLazyGridState()

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = t("Search Posts"),
                showBackButton = true,
                onBackClicked = { navController.popBackStack() },
                showSearchButton = false,
                onMoreClicked = { /* Handle more options */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it.trim() }, // Trim to avoid blank spaces causing unnecessary recomposition
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        // Trigger the search explicitly
                        viewModel.searchPosts(searchText)
                    }),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(Color.White, MaterialTheme.shapes.small)
                        .border(1.dp, Color.LightGray)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text("Search posts...", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Search results
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    searchResults.loadState.refresh is LoadState.Loading -> {
                        CircularProgressIndicator()
                    }
                    searchResults.loadState.refresh is LoadState.Error -> {
                        val error = (searchResults.loadState.refresh as LoadState.Error).error
                        Text(text = "Error loading results: ${error.localizedMessage}")
                    }
                    searchResults.itemSnapshotList.isEmpty() -> {
                        Text(text = "No results found", color = Color.Gray)
                    }
                    else -> {
                        PostGrid(
                            posts = searchResults,
                            onClick = { postId ->
                                navController.navigate(PostScreens.PostDetail.createRoute(postId))
                            },
                            gridState = gridState // Pass the LazyGridState here
                        )
                    }
                }
            }
        }
    }
}

