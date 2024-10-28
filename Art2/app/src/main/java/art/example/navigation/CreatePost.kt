package art.example.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import art.example.ViewModel.PostViewModel
import art.example.api.data.DTO.PostDTO
import org.koin.androidx.compose.koinViewModel

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CreatePost(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") } // Handle image URL

    val postViewModel: PostViewModel = koinViewModel()
    val isLoading by postViewModel.isLoading.observeAsState(false)
    val errorMessage by postViewModel.errorMessage.observeAsState()
    val tags by postViewModel.tags.observeAsState(emptyList())

    // Track selected tags using a set for better performance and avoiding duplicates
    var selectedTags by remember { mutableStateOf(setOf<Long>()) }

    // Load tags when the composable is first launched
    LaunchedEffect(Unit) {
        postViewModel.loadTags()
    }

    // Using Scaffold for top bar and padding
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Create Post",
                showBackButton = true,
            )
        },
    ) { paddingValues ->
        // Use a Box to ensure the button is at the bottom
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Title Input
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description Input
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Image URL Input
                TextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tag Selection
                Text(text = "Select Tags:", fontSize = 18.sp)

                LazyColumn {
                    items(tags) { tag ->
                        // Check if the tag is selected
                        val isSelected = selectedTags.contains(tag.id) // Assuming Tag has an id property
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    selectedTags = if (isSelected) {
                                        selectedTags - tag.id // Deselect the tag
                                    } else {
                                        selectedTags + tag.id // Select the tag
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null // Handled in the Row's click listener
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = tag.name) // Assuming Tag has a name property
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        val postDTO = PostDTO(
                            title = title,
                            description = description,
                            tagsId = selectedTags.toList(), // Convert set to list
                        )

                        // Call the submitPost method
                        postViewModel.submitPost(postDTO, imageUrl)

                        // Navigate back or perform other actions
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.End) // Align button to the right
                ) {
                    Text(text = "Submit Post")
                }


                errorMessage?.let {
                    Text(text = it, color = Color.Red, fontSize = 16.sp) // Show error message
                }
            }
        }
    }
}
