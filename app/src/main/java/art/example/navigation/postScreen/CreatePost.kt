package art.example.navigation.postScreen

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import art.example.ViewModel.PostViewModel
import art.example.ViewModel.TagViewModel
import art.example.api.data.DTO.PostDTO
import art.example.api.data.Tag
import art.example.modules.AnalyticsLogger
import art.example.navigation.MyTopAppBar
import art.example.screen.PostScreens
import art.example.screen.Screen
import org.koin.androidx.compose.koinViewModel

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CreatePost(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null)}

    val postViewModel: PostViewModel = koinViewModel()
    val tagViewModel: TagViewModel = koinViewModel()
    val isLoading by postViewModel.isLoading.observeAsState(false)
    val errorMessage by postViewModel.errorMessage.observeAsState()
    val tags by tagViewModel.tags.observeAsState(emptyList())

    // Track selected tags using a set for better performance and avoiding duplicates
    var selectedTags by remember { mutableStateOf(setOf<Long>()) }



    val context = LocalContext.current
    // Load tags when the composable is first launched
    LaunchedEffect(Unit) {
        tagViewModel.loadTags()
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



                Button(onClick = {
                    // Call the function and pass a lambda to update imageBitmap
                    pasteImageFromClipboard(context) { bitmap ->
                        imageBitmap = bitmap
                    }
                }) {
                    Text("Paste Image")
                }

                // If an image is pasted, show a preview
                imageBitmap?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = "Pasted Image")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Use the custom TagSelectionMenu for selecting tags
                TagSelectionMenu(
                    tags = tags,
                    selectedTags = selectedTags,
                    onTagSelected = { newSelectedTags ->
                        selectedTags = newSelectedTags // Update selected tags
                    },
                    onDismiss = {}
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        val postDTO = PostDTO(
                            title = title,
                            description = description,
                            tagsId = selectedTags.toList(), // Convert set to list
                        )

                        if (imageBitmap != null){
                            postViewModel.submitPost(postDTO, imageBitmap!!)
                        }
                        // Navigate back or perform other actions
                        navController.navigate(PostScreens.PostsScreen.route)
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

@Composable
fun TagSelectionMenu(
    tags: List<Tag>, // List of tags to display
    selectedTags: Set<Long>, // Set of selected tags
    onTagSelected: (Set<Long>) -> Unit, // Callback to update selected tags
    onDismiss: () -> Unit // Callback to dismiss the Popup
) {
    var showTagSelectionMenu by remember { mutableStateOf(false) } // State to control the visibility of the Popup

    // Button to trigger the Popup visibility
    Button(onClick = { showTagSelectionMenu = true }) {
        Text("Select Tags")
    }

    // Show the Popup when showTagSelectionMenu is true
    if (showTagSelectionMenu) {
        Popup(
            alignment = Alignment.TopCenter, // Aligning the popup above
            onDismissRequest = {
                showTagSelectionMenu = false // Close the popup when dismissed
                onDismiss() // Trigger the onDismiss callback
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.medium)
                    .border(1.dp, Color.Gray, MaterialTheme.shapes.medium)
            ) {
                Column {
                    // Make the dropdown scrollable with LazyColumn
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .padding(8.dp)
                    ) {
                        items(tags) { tag ->
                            val isSelected = selectedTags.contains(tag.id)

                            // List item to select/deselect a tag
                            DropdownMenuItem(
                                onClick = {
                                    // Toggle selection state for the tag
                                    val newSelectedTags = if (isSelected) {
                                        selectedTags - tag.id // Deselect the tag
                                    } else {
                                        selectedTags + tag.id // Select the tag
                                    }
                                    onTagSelected(newSelectedTags) // Pass updated selected tags
                                },
                                text = { Text(text = tag.name) }, // Display tag name
                                modifier = Modifier.fillMaxWidth(), // Modify the width to fill the menu
                                leadingIcon = {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null // Handled in the DropdownMenuItem's click listener
                                    )
                                },
                                trailingIcon = {
                                    if (isSelected) {
                                        Icon(Icons.Filled.Check, contentDescription = "Selected")
                                    }
                                },
                                enabled = true, // Enable all menu items
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp) // Add padding
                            )
                        }
                    }

                    // Done button to close the popup
                    Button(
                        onClick = { showTagSelectionMenu = false },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}



fun pasteImageFromClipboard(context: Context, onImageUpdated: (Bitmap?) -> Unit) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // Check if the clipboard has valid data
    if (clipboard.hasPrimaryClip()) {
        val clipData = clipboard.primaryClip
        val item = clipData?.getItemAt(0)

        // Check if the clipboard item is a URI (image data)
        val uri = item?.uri
        if (uri != null) {
            // Try to load the image from the URI
            try {
                val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
                onImageUpdated(bitmap) // Update the image with the retrieved bitmap
            } catch (e: Exception) {
                // Handle error gracefully if the image loading fails
                Toast.makeText(context, "Failed to load image from clipboard", Toast.LENGTH_SHORT).show()
                onImageUpdated(null) // Optionally, clear the image or handle accordingly
            }
        } else {
            // The clipboard data is not a valid image URI
            Toast.makeText(context, "No image found in clipboard", Toast.LENGTH_SHORT).show()
            onImageUpdated(null) // Optionally, clear the image or handle accordingly
        }
    } else {
        // Clipboard is empty or doesn't contain valid data
        Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
        onImageUpdated(null) // Optionally, clear the image or handle accordingly
    }
}
