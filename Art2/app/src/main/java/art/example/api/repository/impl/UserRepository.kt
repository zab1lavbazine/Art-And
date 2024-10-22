package art.example.api.repository.impl

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsClient
import art.example.api.data.User
import art.example.api.reponses.UserCredentials
import art.example.api.repository.IUserApiService
import art.example.api.service.UserApiService
import art.example.database.UserDao.UserDao
import art.example.database.UserEntity
import art.example.database.toUser
import art.example.navigation.LoginScreen
import retrofit2.HttpException

class UserRepository(
    private val userApiService: UserApiService,
    private val userDao: UserDao, // Inject the DAO
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

    // Inside UserRepository
    fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }


    // Get all users, either from the API or from the local database if offline
    override suspend fun getUsers(): List<User> {
        return try {
            // Fetch from API
            val users = userApiService.getUsers()
            // Save to local database
            users.forEach { userDao.insertUser(UserEntity(it.id, it.username, it.email)) }
            users
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching users from API, loading from local DB", e)
            // Fallback to local database
            userDao.getUsersWithTags().map { it.toUser() } // Convert UserEntity to User
        }
    }

    // Get a specific user either from the API or fallback to the local database
    override suspend fun getUserById(id: Long): User? {
        return try {
            val user = userApiService.getUserById() // Fetch from API
            user?.let { userDao.insertUser(UserEntity(it.id, it.username, it.email)) } // Save to DB
            user
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user from API, loading from local DB", e)
            // Fallback to local database
            userDao.getUserByIdWithTags(id)?.toUser() // Convert UserEntity to User
        }
    }

    override suspend fun login(username: String, password: String): User? {
        val credentials = UserCredentials(username, password)
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
            val loggedInUser = userApiService.getUserByUsername()

            // Save the user credentials
            if (loggedInUser != null) {
                saveUserCredentials(loggedInUser, password)
                currentUser = loggedInUser // Cache the current user

                // Save the logged-in user to local database
                userDao.insertUser(UserEntity(loggedInUser.id, loggedInUser.username, loggedInUser.email))
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
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    private fun clearSavedCredentials() {
        sharedPreferences.edit().clear().apply()
        credentialsClient.disableAutoSignIn()
    }
}
