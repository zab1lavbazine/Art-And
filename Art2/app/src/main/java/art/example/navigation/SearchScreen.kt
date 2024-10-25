package art.example.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import art.example.ViewModel.PostViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<String>()) } // To hold search results
    val postViewModel: PostViewModel = koinViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Search Posts",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Using the SearchBar component
        SearchBar(
            inputField = {
                BasicTextField(
                    value = searchText,
                    onValueChange = { text ->
                        searchText = text
                        searchResults = performSearch(searchText) // Example search function
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp)),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text("Type to search...", color = Color.Gray)
                        }
                        innerTextField() // Draw the inner text field
                    }
                )
            },
            expanded = true, // Keep it expanded for simplicity; you can manage this state as needed
            onExpandedChange = {},
            content = {
                // Displaying Search Results
                if (searchResults.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Results:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        for (result in searchResults) {
                            Text(
                                text = result,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    if (searchText.isNotEmpty()) {
                        Text(
                            text = "No results found.",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        )
    }
}

// Simulated search function
fun performSearch(query: String): List<String> {
    // Replace this with actual search logic. For now, return dummy data.
    val allPosts = listOf("Post 1", "Post 2", "Post 3") // Replace with actual data
    return if (query.isEmpty()) {
        emptyList()
    } else {
        allPosts.filter { it.contains(query, ignoreCase = true) } // Filter posts based on query
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    inputField: @Composable () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp), // Rounded corners
    colors: SearchBarColors = SearchBarDefaults.colors(),
    tonalElevation: Dp = 4.dp,
    shadowElevation: Dp = 4.dp,
    windowInsets: WindowInsets = SearchBarDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(tonalElevation, shape) // Add shadow for depth
            .padding(8.dp)
            .background(colors.containerColor, shape)
            .border(BorderStroke(1.dp, Color.LightGray), shape) // Use light gray border
            .padding(windowInsets.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input Field
        Box(modifier = Modifier.fillMaxWidth()) {
            inputField()
        }

        // Expandable content that shows when the search bar is expanded
        if (expanded) {
            Column {
                content()
            }
        }
    }
}
