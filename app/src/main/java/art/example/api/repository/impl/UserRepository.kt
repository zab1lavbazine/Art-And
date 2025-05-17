package art.example.api.repository.impl

import android.content.Context
import android.util.Log
import androidx.room.Transaction
import art.example.api.data.ResetPasswordRequest
import art.example.api.data.SelectedUser
import art.example.api.data.User
import art.example.api.data.toTagEntity
import art.example.api.reponses.RegisterUserDTO
import art.example.api.reponses.UserCredentials
import art.example.api.repository.IUserRepository
import art.example.api.service.UserApiService
import art.example.database.TagDao.TagDao
import art.example.database.UserDao.UserDao
import art.example.database.entities.toTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.security.MessageDigest
import androidx.core.content.edit

class UserRepository(
    private val userApiService: UserApiService,
    private val userDao: UserDao, // Inject the DAO
    private val context: Context
) : IUserRepository {

    private var currentUser: User? = null
    private var authToken: String? = null

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USERNAME = "KEY_USERNAME"
        private const val KEY_EMAIL = "KEY_EMAIL"
        private const val KEY_PASSWORD = "KEY_PASSWORD"
    }

    // Save user credentials to SharedPreferences
    private fun saveUserCredentials(user: User, password: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, user.username)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    // Retrieve saved user credentials
    fun getSavedUserCredentials(): UserCredentials? {
        val username = sharedPreferences.getString(KEY_USERNAME, null)
        val email = sharedPreferences.getString(KEY_EMAIL, null)
        val password = sharedPreferences.getString(KEY_PASSWORD, null)

        return if (username != null && email != null && password != null) {
            UserCredentials(username, password)
        } else {
            null
        }
    }

    suspend fun getSelectedUserById(userId: Long) : SelectedUser? {
        val selectedUser = userApiService.getSelectedUserById(userId)
        return selectedUser
    }

    // Inside UserRepository
    fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }


    suspend fun register(userCred: RegisterUserDTO){
        withContext(Dispatchers.IO){
                val newHashedPassword = hashFunction(userCred.password)
                userCred.password = newHashedPassword
                userApiService.register(userCred)
                Log.d("FLOW","Registration successfull")
        }
    }

    @Transaction
    suspend fun updateUserInfo(user: User) : User {
        Log.d("FLOW", "User to send for update: $user")
        val mapWithResponseAndToken = userApiService.updateUserInfo(user)
        Log.d("FLOW", "Map with response: $mapWithResponseAndToken")
        val newToken = mapWithResponseAndToken["token"]
        saveAuthTokenToSharedPreferences(newToken!!)
        createUser(user)
        currentUser = user
        return user
    }

    suspend fun resetPassword(email: String): String? {
        val response = userApiService.resetPassword(email)
        return response
    }

    suspend fun sendNewPassword(request: ResetPasswordRequest) {
        val newPassword = hashFunction(request.password)
        val newHashConfirmPassword = hashFunction(request.confirmPassword)
        val newRequest = request.copy(password = newPassword, confirmPassword = newHashConfirmPassword)
        return userApiService.sendNewPassword(newRequest)
    }


    // Get a specific user either from the API or fallback to the local database
    @Transaction
    suspend fun getUserAccount(userId: Long): User? {
         try {

            // get from the database
            val currentDatUser = userDao.getUserById(userId)?.toUser()
            if (currentDatUser != null){
                val userTags = userDao.getUserByIdWithTags(userId)
                currentDatUser.preferredTags = userTags.tags.map { it.toTag() }.toMutableList()
                Log.d("FLOW", "Getting database user: $currentDatUser")
                return currentDatUser
            }

            // getting user from api
            val user = userApiService.getUserAccount() // Fetch from API
            Log.d("FLOW", "Fetched user from API: $user")
            createUser(user!!)
            // saving in the current user
            currentUser = user
            // return user
            return user
        } catch (e: Exception) {
            Log.e("FLOW", "Error fetching user from API, loading from local DB", e)
            return null
        }
    }


    private suspend fun createUser(user: User){
        withContext(Dispatchers.IO){
            try {
                user.let {
                    // insert user in the database
                    userDao.insertUserWithTags(user)
                }
            } catch (e : Exception){
                Log.e("FLOW", "error with creating user")
            }
        }
    }


    suspend fun logout() {
        withContext(Dispatchers.IO) {
            try {
                // Clear SharedPreferences
                sharedPreferences.edit() { clear() }

                // Clear in-memory cache
                currentUser = null
                authToken = null

                Log.d("FLOW", "User successfully logged out.")
            } catch (e: Exception) {
                Log.e("FLOW", "Error during logout: ${e.message}")
            }
        }
    }


    override suspend fun login(username: String, password: String): User? {
        val newPassword = hashFunction(password)
        val credentials = UserCredentials(username, newPassword)
        return try {
            Log.d("FLOW", "Logging in user: ${credentials.username} and ${credentials.password}")

            // Call the API to login
            val response = userApiService.login(credentials)
            Log.d("FLOW", "credentials from the api $credentials")

            // Extract the token from the response
            val token = response.token
            authToken = token
            saveAuthTokenToSharedPreferences(authToken!!)

            // Fetch the user details using the token
            val loggedInUser = userApiService.getUserAccount()

            // Save the user credentials
            if (loggedInUser != null) {
                Log.d("FLOW", "CURRENT user : $loggedInUser")
                saveUserCredentials(loggedInUser, password)
                currentUser = loggedInUser // Cache the current user

                // Save the logged-in user to local database
                userDao.insertUser(loggedInUser.toUserEntity())
            }
            loggedInUser
        } catch (e: HttpException) {
            if (e.code() == 403) {
                Log.e("FLOW", "403 Error: Access Denied. Redirecting to login.")
                clearSavedCredentials()
                return null
            }
            Log.e("FLOW", "HTTP Error: ${e.code()} - ${e.message()} - ${e.response()?.errorBody()?.string()}")
            return null
        } catch (e: Exception) {
            Log.e("FLOW", "Error logging in user", e)
            Log.d("FLOW", "ERROR : ${e.message}")
            null
        }
    }

    private fun saveAuthTokenToSharedPreferences(token: String) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit() { putString("auth_token", token) }
    }

    private fun clearSavedCredentials() {
        sharedPreferences.edit() { clear() }
    }


    private fun hashFunction(password: String): String{
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it)}
    }
}
