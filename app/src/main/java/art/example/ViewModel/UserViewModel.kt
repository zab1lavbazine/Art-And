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
import art.example.api.data.ResetPasswordRequest
import art.example.api.data.SelectedUser
import art.example.api.data.User
import art.example.api.reponses.RegisterUserDTO
import art.example.api.reponses.UserCredentials
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
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

    private val _apiResponse = MutableLiveData<String?>()
    val apiResponse: LiveData<String?> get() = _apiResponse

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _selectedUser = MutableLiveData<SelectedUser?>()
    val selectedUser: LiveData<SelectedUser?> get() = _selectedUser



    fun getSelectedUserById (userId: Long){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val selectedUser = userRepository.getSelectedUserById(userId)
                if (selectedUser != null){
                    _selectedUser.value = selectedUser
                }
                Log.d("FLOW", "Selected user $selectedUser")
            } catch (e : Exception){
                Log.d("FLOW", "Error getting selected user by id: $userId")
                Log.d("FLOW", "Error $e")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun getSavedUser(): UserCredentials? {
        return userRepository.getSavedUserCredentials()
    }


    fun updateUserInfo(user: User){
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val newUser = userRepository.updateUserInfo(user)
                _currentUser.value = newUser
                Log.d("FLOW", "Updated user: $newUser")
            } catch (e: Exception){
                Log.d("FLOW", "user update failed user: $user, error: ${e.message}")
                _errorMessage.value = e.message
            }
        }
    }



    fun getCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Get the saved user ID from SharedPreferences
                val userId = sharedPreferences.getLong("current_user_id", -1)
                Log.d("FLOW", "Current user ID: $userId")
                if (userId != -1L) {
                    val user = userRepository.getUserAccount(userId)
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



    fun register(email : String, username: String, password: String, onRegisterSuccess: () -> Unit ){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val registerUser = RegisterUserDTO(email = email, username = username, password = password)
                userRepository.register(registerUser)
                Log.d("FLOW", "REGISTRATION successfully")
                onRegisterSuccess()
            } catch (e : Exception){
                Log.e("FLOW", "Error with registration: $e")
                _errorMessage.value = "Failed to register"
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
                // adding timeout -> not have infinite loading
                withTimeout(5000L) {
                    val loggedInUser = userRepository.login(username, password)
                    if (loggedInUser != null) {
                        _currentUser.value = loggedInUser // Update currentUser LiveData
                        setAuthToken(userRepository.getAuthToken() ?: "")
                        saveUserCredentials(loggedInUser) // Save user credentials
                        onLoginSuccess(userRepository.getAuthToken() ?: "")
                    } else {
                        _errorMessage.value = "Login failed. Please try again."
                    }
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


    fun logout(onLogoutComplete: ()-> Unit) {
        viewModelScope.launch {
            try {
                userRepository.logout()
                // deleting current user from the memory
                _currentUser.value = null
                onLogoutComplete()
            } catch (e: Exception){
                Log.d("FLOW", "Logout from the application")
            }
        }
    }


    fun resetPassword(email: String){
        viewModelScope.launch {
            _errorMessage.value = null
            _apiResponse.value = null
            try {
                val response = userRepository.resetPassword(email)
                _apiResponse.value = response
            } catch (e: Exception){
                Log.d("FLOW", "Failed to send reset password request")
                _errorMessage.value = "Failed to reset password"
            }
        }
    }

    fun sendNewPassword(
        token: String,
        password: String,
        confirmedPassword: String
    ){
        viewModelScope.launch {
            _errorMessage.value = null
            _apiResponse.value = null
            try {
                val request = ResetPasswordRequest(
                    token = token,
                    password = password,
                    confirmPassword = confirmedPassword
                )
                userRepository.sendNewPassword(request)
            } catch (e : Exception){
                Log.d("FLOW", "Failed to send new password with token")
                Log.d("FLOW", "Error with sending: ${e.message}")
                _errorMessage.value = "Failed to update password on api"
            }
        }
    }


}
