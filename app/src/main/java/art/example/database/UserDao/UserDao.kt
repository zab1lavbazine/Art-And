package art.example.database.UserDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import art.example.api.data.User
import art.example.api.data.toTagEntity
import art.example.database.entities.FolderEntity
import art.example.database.entities.FolderWithPosts
import art.example.database.entities.TagEntity
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

    @Transaction
    suspend fun insertUserWithTags(user: User){
        // deleting old tags from the user
        deleteTagsForUser(user.id)
        // inserting user to database
        insertUser(user.toUserEntity())
        // insert tags to database
        user.preferredTags.forEach { tag ->
            insertTag(tag.toTagEntity())
            insertUserWithTag(UserWithTagsCrossRef(user.id, tag.id))
        }
    }

    @Query("DELETE FROM UserWithTagsCrossRef where userId = :userId")
    suspend fun deleteTagsForUser(userId: Long)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tagEntity: TagEntity)


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
