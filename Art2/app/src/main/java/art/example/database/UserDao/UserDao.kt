package art.example.database.UserDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import art.example.database.entities.FolderEntity
import art.example.database.entities.FolderWithPosts
import art.example.database.entities.UserEntity
import art.example.database.entities.UserWithFolders
import art.example.database.entities.UserWithPosts
import art.example.database.entities.UserWithTags
import art.example.database.entities.UserWithTagsCrossRef


@Dao
interface UserDao {

    // managing user

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)


    // user and tag
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserWithTag(userWithTags: UserWithTagsCrossRef)



    // To get a specific user and their tags by ID
    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserByIdWithTags(userId: Long): UserWithTags


    // To get a specific user by ID
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(folders: List<FolderEntity>)


    @Query("SELECT * FROM folders WHERE folderId = :folderId")
    suspend fun getFolderById(folderId: Long) : FolderEntity

    @Query("SELECT * FROM folders WHERE folderId = :folderId")
    suspend fun getDetailedFolderById(folderId: Long): FolderWithPosts


    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserWithPosts(userWithPosts: UserWithTagsCrossRef)

    @Transaction
    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserByIdWithPosts(id: Long): UserWithPosts


    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserByIdWithFolders(userId: Long): UserWithFolders?


}
