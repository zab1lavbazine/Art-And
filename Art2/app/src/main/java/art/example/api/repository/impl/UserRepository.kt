package art.example.api.repository.impl

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsClient
import art.example.api.data.User
import art.example.api.reponses.UserCredentials
import art.example.api.repository.IUserApiService
import art.example.api.service.UserApiService
import art.example.database.UserDao.UserDao

class UserRepository(
    private val userApiService: UserApiService,
    private val userDao: UserDao,
    private val context: Context
) : IUserApiService {

    private var currentUser: User? = null
    private var authToken: String? = null
    private val credentialsClient: CredentialsClient = Credentials.getClient(context)

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

    override suspend fun login(username: String, password: String): User? {
        val credentials = UserCredentials(username, password)
        return try {
            Log.d("UserRepository", "Logging in user: $username")

            // Call the API to login
            val response = userApiService.login(credentials)

            // Extract the token from the response
            val token = response.token

            // Save the token for future requests
            authToken = token
            saveAuthTokenToSharedPreferences(authToken!!)

            // Fetch the user details using the token
            val loggedInUser = userApiService.getUserByUsername()

            // Save the user credentials
            if (loggedInUser != null) {
                saveUserCredentials(loggedInUser, password)
                currentUser = loggedInUser // Cache the current user
            }
            loggedInUser
        } catch (e: Exception) {
            Log.e("UserRepository", "Error logging in user", e)
            null // Handle exceptions
        }
    }

    private fun saveAuthTokenToSharedPreferences(token: String) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    // Fetch user from the database by ID
    override suspend fun getUserById(id: Long): User? {
        return try {
            val userEntity = userDao.getUserById(id)
            userEntity?.let {
                User(it.id, it.username, it.email)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user by ID from database", e)
            null
        }
    }

    override suspend fun getUsers(): List<User> {
        return try {
            val userEntities = userDao.getAllUsers()
            userEntities.map { User(it.id, it.username, it.email) }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching users from database", e)
            emptyList()
        }
    }

    // Get the current logged-in user
    fun getCurrentUser(): User? {
        return currentUser
    }

    // Get the current auth token
    fun getAuthToken(): String? {
        return authToken
    }

    // Clear user credentials and token
    fun clearUserSession() {
        currentUser = null
        authToken = null
        clearSavedCredentials()
    }

    private fun clearSavedCredentials() {
        sharedPreferences.edit().clear().apply()
        credentialsClient.disableAutoSignIn()
    }
}
