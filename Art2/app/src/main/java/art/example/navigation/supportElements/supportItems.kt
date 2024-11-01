package art.example.navigation.supportElements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import art.example.navigation.MenuItem
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
