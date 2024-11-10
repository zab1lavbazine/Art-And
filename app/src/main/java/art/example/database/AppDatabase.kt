package art.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import art.example.database.PostDao.PostDao
import art.example.database.TagDao.TagDao
import art.example.database.UserDao.UserDao
import art.example.database.entities.FolderEntity
import art.example.database.entities.FolderWithPostsCrossRef
import art.example.database.entities.ImageEntity
import art.example.database.entities.PostEntity
import art.example.database.entities.PostWithTags
import art.example.database.entities.TagEntity
import art.example.database.entities.UserEntity
import art.example.database.entities.UserWithTagsCrossRef
import art.example.database.folderDao.FolderDao

@Database(
    entities = [
        // user includes
        UserEntity::class,
        UserWithTagsCrossRef::class,
        // tag include
        TagEntity::class,
        // post includes
        PostEntity::class,
        ImageEntity::class,
        PostWithTags::class,
        // folder entity
        FolderEntity::class,
        FolderWithPostsCrossRef::class,
    ],
    version = 1 // Increment the version number
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun tagDao(): TagDao
    abstract fun folderDao(): FolderDao
    // Add other DAOs as needed (e.g., TagDao, PostDao)

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // Name of the database
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}