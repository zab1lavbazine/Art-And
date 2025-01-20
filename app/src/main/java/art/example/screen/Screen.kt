package art.example.screen

sealed class Screen(val route: String)

interface AuthScreens {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ResetPassword: Screen("reset_password")
    data object NewPassword: Screen("new_password")
}

interface PostScreens {
    data object PostsScreen : Screen("posts")
    data object PostDetail : Screen("postDetail/{postId}") {
        fun createRoute(postId: Long) = "postDetail/$postId"
    }
    data object CreatePost : Screen("posts/createPost")
}

interface FolderScreens {
    data object FolderDetail : Screen("folderDetail/{folderId}") {
        fun createRoute(folderId: Long) = "folderDetail/$folderId"
    }
    data object CreateFolder : Screen("createFolder")
}

interface UserScreens {
    data object UserDetail : Screen("user/{userId}") {
        fun createRoute(userId: Long) = "user/$userId"
    }
    data object MyProfile : Screen("profile")
}

interface MiscScreens {
    data object SearchScreen : Screen("search")
    data object HelloScreen : Screen("helloScreen")
}
