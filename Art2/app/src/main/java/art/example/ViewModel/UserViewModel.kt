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
import art.example.api.reponses.UserCredentials
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
    private val context: Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    // LiveData for the selected user
    private val _selectedUser = MutableLiveData<User?>()
    val selectedUser: LiveData<User?> get() = _selectedUser

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingPosts = MutableLiveData<Boolean>(false)
    val isLoadingPosts: LiveData<Boolean> = _isLoadingPosts


    private val _isLoadingFolder = MutableLiveData<Boolean>(false)
    val isLoadingFolder:LiveData<Boolean> = _isLoadingFolder

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser


    private val _selectedFolder = MutableLiveData<Folder?>()
    val selectedFolder: LiveData<Folder?> get() = _selectedFolder


    private val _userFolders = MutableLiveData<List<Folder>>()
    val userFolders: LiveData<List<Folder>> get() = _userFolders



    fun getUserFolders() {
        viewModelScope.launch {
            _isLoadingPosts.value = true
            try {
                val fetchedFolders = userRepository.getCurrentUserFolders()
                _userFolders.value = fetchedFolders
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoadingPosts.value = false
            }
        }
    }



    fun getSavedUser(): UserCredentials? {
        return userRepository.getSavedUserCredentials()
    }


    fun getDetailedFolder(folderId: Long){
        viewModelScope.launch {
            _isLoadingFolder.value = true
            try {
                val fetchedFolder = userRepository.getFolderById(folderId)
                _selectedFolder.value = fetchedFolder
            } catch (e: Exception){
                Log.d("FLOW", "ERROR with fetching folder with id: $folderId")
            } finally {
                _isLoadingFolder.value = false
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get the saved user ID from SharedPreferences
                val userId = sharedPreferences.getLong("current_user_id", -1)
                Log.d("FLOW", "Current user ID: $userId")
                if (userId != -1L) {
                    // Fetch the current user from the repository
                    val curr = userRepository.getUserAccount(userId) // Fetch user by ID
                    _currentUser.value = curr
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


    fun savePostInFolder(post: Post, folder: Folder){
        viewModelScope.launch {
            try {
                userRepository.updateFolderWithPost(folder, post)
            }catch (e : Exception){
                Log.e("FLOW", "ERROR with adding post: $post to folder: $folder")
            }
        }

    }


    fun createFolder(title: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Clear previous error message
            try {
                val newFolder = userRepository.createFolder(title, description)
                if (newFolder != null) {
                    // Update the list of user folders
                    val updatedFolders = _userFolders.value?.toMutableList() ?: mutableListOf()
                    updatedFolders.add(newFolder)
                    _userFolders.value = updatedFolders
                } else {
                    _errorMessage.value = "Failed to create folder. Please try again."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create folder. Please try again."
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
