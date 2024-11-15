package art.example.ViewModel

import art.example.api.repository.impl.UserRepository
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import art.example.api.data.Folder
import art.example.api.data.Post
import art.example.api.data.User
import art.example.api.reponses.RegisterUserDTO
import art.example.api.reponses.UserCredentials
import kotlinx.coroutines.launch
import java.security.MessageDigest

class UserViewModel(
    private val userRepository: UserRepository,
    private val context: Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser





//
//    fun getUserFolders() {
//        viewModelScope.launch {
//            _isLoadingPosts.value = true
//            try {
//                val fetchedFolders = userRepository.getCurrentUserFolders()
//                _userFolders.value = fetchedFolders
//            } catch (e: Exception) {
//                // Handle error
//            } finally {
//                _isLoadingPosts.value = false
//            }
//        }
//    }
//
//    fun getUserFoldersById(userId: Long){
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val userFolders = userRepository.getUserFoldersById(userId)
//               _userFolders.value = userFolders
//                Log.d("FLOW", "USER folders $userFolders")
//            } catch(e : Exception){
//                Log.d("FLOW", "ERROR getting folders for userId: $userId")
//
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }



    fun getSavedUser(): UserCredentials? {
        return userRepository.getSavedUserCredentials()
    }


//    fun getDetailedFolder(folderId: Long){
//        viewModelScope.launch {
//            _isLoadingFolder.value = true
//            try {
//                val fetchedFolder = userRepository.getFolderById(folderId)
//                _selectedFolder.value = fetchedFolder
//            } catch (e: Exception){
//                Log.d("FLOW", "ERROR with fetching folder with id: $folderId")
//            } finally {
//                _isLoadingFolder.value = false
//            }
//        }
//    }


    fun getCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Get the saved user ID from SharedPreferences
                val userId = sharedPreferences.getLong("current_user_id", -1)
                Log.d("FLOW", "Current user ID: $userId")
                if (userId != -1L) {
                    val user = userRepository.getUserAccount()
                    _currentUser.value = user
                } else {
                    _currentUser.value = null // Handle the case where user ID is invalid
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching current user", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


//    fun savePostInFolder(post: Post, folder: Folder){
//        viewModelScope.launch {
//            try {
//                userRepository.updateFolderWithPost(folder, post)
//            }catch (e : Exception){
//                Log.e("FLOW", "ERROR with adding post: $post to folder: $folder")
//            }
//        }
//    }


//    fun deletePostFromFolder(post: Post, folder: Folder) {
//        viewModelScope.launch {
//            try {
//                Log.d("FLOW", "Deleting post: $post from the folder: $folder")
//                val newFolder = userRepository.deletePostFromFolder(folder, post)
//                _selectedFolder.value = newFolder
//                Log.d("FLOW", "New selected folder $selectedFolder")
//            } catch (e: Exception) {
//                Log.e("FLOW", "Error deleting post: $post from folder: $folder")
//                _errorMessage.value = "Error deleting post. Please try again."
//            }
//        }
//    }
//
//    fun deleteFolderById(folderId : Long){
//        viewModelScope.launch {
//            try {
//                Log.d("FLOW", "DELETING folder with id: $folderId")
//                userRepository.deleteFolderById(folderId)
//            } catch (e: Exception){
//                Log.d("FLOW", "Error while deleting folder by id: $folderId")
//            }
//        }
//    }


//    fun createFolder(title: String, description: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _errorMessage.value = null // Clear previous error message
//            try {
//                val newFolder = userRepository.createFolder(title, description)
//                if (newFolder != null) {
//                    // Update the list of user folders
//                    val updatedFolders = _userFolders.value?.toMutableList() ?: mutableListOf()
//                    updatedFolders.add(newFolder)
//                    _userFolders.value = updatedFolders
//                } else {
//                    _errorMessage.value = "Failed to create folder. Please try again."
//                }
//            } catch (e: Exception) {
//                _errorMessage.value = "Failed to create folder. Please try again."
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

//    fun updateFolderInfo(id: Long, folderTitle: String, folderDescription: String) {
//        viewModelScope.launch {
//            _errorMessage.value = null
//            try {
//                val newFolder = userRepository.updateFolderById(id, folderTitle, folderDescription)
//                Log.d("FLOW", "NEW folder from the api folder: $newFolder")
//                _selectedFolder.value = newFolder
//            } catch( e: Exception){
//                Log.d("FLOW", "error with updating folder by id: $id")
//                _errorMessage.value = "Failed to update folder, try again"
//            }
//        }
//    }


    fun register(email : String, username: String, password: String, onRegisterSuccess: () -> Unit ){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val registerUser = RegisterUserDTO(email = email, username = username, password = password)
                userRepository.register(registerUser)
                Log.d("FLOW", "REGISTRATION successfully")
                onRegisterSuccess()
            } catch (e : Exception){
                Log.e("FLOW", "Error with registration")
                _errorMessage.value = "Registration failed, please try again"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(username: String, password: String, onLoginSuccess: (String) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            _errorMessage.value = null // Clear previous error message
            try {
                val loggedInUser = userRepository.login(username, password)
                if (loggedInUser != null) {
                    _currentUser.value = loggedInUser // Update currentUser LiveData
                    setAuthToken(userRepository.getAuthToken() ?: "")
                    saveUserCredentials(loggedInUser) // Save user credentials
                    onLoginSuccess(userRepository.getAuthToken() ?: "")
                } else {
                    _errorMessage.value = "Login failed. Please try again."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Login failed. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Save user credentials to SharedPreferences
    private fun saveUserCredentials(user: User) {
        with(sharedPreferences.edit()) {
            putLong("current_user_id", user.id)
            putString("current_user_username", user.username)
            putString("current_user_email", user.email)
            apply()
        }
    }


    fun setAuthToken(token: String) {
        with(sharedPreferences.edit()) {
            putString("auth_token", token) // Save token in SharedPreferences
            apply() // or commit() if you need to wait for the write to finish
        }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null) // Retrieve the token
    }

    fun logout() {
        _currentUser.value = null // Clear current user from LiveData
        with(sharedPreferences.edit()) {
            remove("current_user_id")
            remove("current_user_username")
            remove("current_user_email")
            remove("auth_token")
            apply() // Save changes
        }
    }


}
