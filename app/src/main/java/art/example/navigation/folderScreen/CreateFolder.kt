package art.example.navigation.folderScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import art.example.ViewModel.FolderViewModel
import art.example.navigation.MyTopAppBar
import cz.fit.cvut.feature.translation.presentation.common.component.t
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateFolder(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val folderViewMode: FolderViewModel = koinViewModel()

    val isloading by folderViewMode.isLoading.observeAsState(false)
    val errorMessage by folderViewMode.errorMessage.observeAsState()

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = t("Create Folder"),
                showBackButton = true,
                onBackClicked = {
                    navController.popBackStack()
                }
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
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { t("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { t("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Create button
                Button(
                    onClick = {
                        folderViewMode.createFolder(title, description)
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    t("Create")
                }

                // Show loading indicator if needed
                if (isloading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                // Show error message if any
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = it)

                }
            }

        }
    }
}