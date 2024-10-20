package art.example.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import art.example.api.data.User
import art.example.api.reponses.UserCredentials
import art.example.api.repository.impl.UserRepository
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

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetchedUsers = userRepository.getUsers()
                _users.value = fetchedUsers
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.getUserById(id)
                // Check if the fetched user is different from the current selected user
                if (_selectedUser.value != user) {
                    _selectedUser.value = user // Update LiveData with the new user
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSavedUser(): UserCredentials? {
        return userRepository.getSavedUserCredentials()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get the saved user ID from SharedPreferences
                val userId = sharedPreferences.getLong("current_user_id", -1)
                if (userId != -1L) {
                    // Fetch the current user from the repository
                    val curr = userRepository.getUserById(userId) // Fetch user by ID
                    Log.d("UserViewModel", "Fetched current user: $curr")
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
