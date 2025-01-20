package art.example.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import art.example.api.data.Folder
import art.example.api.data.Post
import art.example.api.repository.impl.FolderRepository
import kotlinx.coroutines.launch

class FolderViewModel(
    private val folderRepository: FolderRepository,
    private val context: Context,
) : ViewModel(){

    private val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)


    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage


    private val _selectedFolder = MutableLiveData<Folder?>()
    val selectedFolder: LiveData<Folder?> get() = _selectedFolder


    private val _folders = MutableLiveData<List<Folder>>()
    val folders: LiveData<List<Folder>> get() = _folders



    suspend fun getFoldersByUserId(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetchedFolders = folderRepository.getFoldersByUserId(userId)
                _folders.value = fetchedFolders
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

     fun getCurrentUserFolders(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserId = sharedPreferences.getLong("current_user_id", -1)
                if (currentUserId != -1L) {
                    Log.d("FLOW", "Getting folders for current user")
                    val fetchedFolders = folderRepository.getFoldersByUserId(currentUserId)
                    _folders.value = fetchedFolders
                } else {
                    Log.d("FLOW", "Getting folders for current null")
                    _folders.value = emptyList()
                }
            } catch (e: Exception) {
                // Handle error
                Log.d("FLOW", "Error with fetching current user folders")
                _folders.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

     fun getDetailedFolderById(folderId: Long){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val folder = folderRepository.getDetailedFolderById(folderId)
                Log.d("FLOW", "Getting detailed folder by id: $folderId, folder: $folder")
                _selectedFolder.value = folder
            } catch (e: Exception){
                _errorMessage.value = e.message
                Log.d("FLOW", "failed to get detailed folder by id: $folderId")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteFolderById(folderId: Long){
        viewModelScope.launch {
            try{
                folderRepository.deleteFolderById(folderId)
            } catch (e: Exception){
                Log.d("FLOW", "failed to get delete folder by id: $folderId")
                _errorMessage.value = e.message
            }
        }
    }

    fun deletePostFromFolder(post: Post, folder: Folder){
        viewModelScope.launch {
            try {
                val currentUserId = sharedPreferences.getLong("current_user_id", -1)
                val newFolder = folderRepository.deletePostFromFolder(post, folder, currentUserId)
                Log.d("FLOW", "Deleted from folder: $folder")
                _selectedFolder.value = newFolder
            } catch (e: Exception){
                Log.d("FLOW", "Error deleting post from folder")
            }
        }
    }

    fun updateFolderInfo(id: Long, title: String, description: String){
        viewModelScope.launch {
            try {
                val currentUserId = sharedPreferences.getLong("current_user_id", -1)
                val newFolder = folderRepository.updateFolderInfo(id, title, description, currentUserId)
                _selectedFolder.value = newFolder
            } catch (e: Exception){
                Log.d("FLOW", "Error updating folder info")
            }
        }
    }

    fun createFolder(title: String, description: String){
        viewModelScope.launch {
            try {
                val currentUserId = sharedPreferences.getLong("current_user_id", -1)
                if(currentUserId != -1L){
                    folderRepository.createFolderForUser(title, description, currentUserId)
                }
                Log.d("FLOW", "Folder created")
            } catch (e: Exception){
                Log.d("FLOW", "error creating folder for user")
            }
        }
    }

     fun savePostInFolder(post: Post, folder: Folder){
        viewModelScope.launch {
            try {
                val currentUserId = sharedPreferences.getLong("current_user_id", -1)
                folderRepository.savePostInFolder(post, folder, currentUserId)
                Log.d("FLOW", "Saving post in the folder post: $post , folder: $folder")
            }catch (e: Exception){
                Log.d("FLOW", "Error with saving post in folder")

            }
        }
    }

}