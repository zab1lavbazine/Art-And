package art.example.navigation.supportElements

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import art.example.api.data.Post
import art.example.navigation.MenuItem
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyModalBottomSheet(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    menuItems: List<MenuItem>
) {
    if (showDialog) {

        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        val coroutineScope = rememberCoroutineScope()


        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    onDismissRequest()
                }
            },
            sheetState = bottomSheetState,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Options",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()

                    // Dynamically add menu items as TextButtons
                    menuItems.forEach { item ->
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                item.onClick()
                                onDismissRequest() // Close dialog after action
                            }
                        ) {
                            Text(item.label)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Close button
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}


@Composable
fun ResolvePostImage(post: Post, maxCardHeight: Dp = 200.dp) {

    if (post.image?.file != null) {
        val imageUrl = post.image.file
        if (imageUrl.isEmpty()) {
            Placeholder() // Show a placeholder if the URL is null or empty
        } else {
            // Coil image request to load the image
            val imagePainter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl) // The image URL can be either a file path or a URL
                    .crossfade(true) // Enable smooth transition
                    .build()
            )

            // Display the image
            Image(
                painter = imagePainter,
                contentDescription = "Post Image",
                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = maxCardHeight), // Adjust to your layout
                contentScale = ContentScale.Crop
            )
        }
    } else {
        Placeholder()
    }
}


@Composable
fun Placeholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No Image Available",
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
        )
    }
}
