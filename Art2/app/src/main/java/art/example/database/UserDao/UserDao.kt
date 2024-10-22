package art.example.database.UserDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import art.example.database.PostEntity
import art.example.database.TagEntity
import art.example.database.UserEntity
import art.example.database.UserWithTags


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    // To get users with their tags
    @Transaction
    @Query("SELECT * FROM users")
    suspend fun getUsersWithTags(): List<UserWithTags>

    // To get a specific user and their tags by ID
    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserByIdWithTags(userId: Long): UserWithTags?

    // To get a specific user by ID
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: Long): UserEntity?
}
