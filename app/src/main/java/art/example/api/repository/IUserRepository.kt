package art.example.api.repository

import art.example.api.data.User

interface IUserRepository {
    suspend fun login(username: String, password: String): User?
}
