package art.example.api.repository

import art.example.api.data.User
import art.example.api.reponses.LoginResponse
import art.example.api.reponses.UserCredentials

interface IUserApiService {
    suspend fun login(username: String, password: String): User?
}
