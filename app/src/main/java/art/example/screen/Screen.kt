package art.example.screen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object PostsScreen : Screen("posts")
    object PostDetail : Screen("postDetail/{postId}") { // Ensure this is correct
        fun createRoute(postId: Long) = "postDetail/$postId"
    }

    object CreatePost : Screen("posts/createPost")
    object FolderDetail: Screen("folderDetail/{folderId}") {
        fun createRoute(folderId: Long) = "folderDetail/$folderId"
    }

    object UserDetail : Screen("user/{userId}"){
        fun createRoute(userId: Long) = "user/$userId"
    }

    object MyProfile: Screen("profile")

    object SearchScreen: Screen("search")

    object RegisterScreen : Screen("register")
    object CreateFolder : Screen("createFolder")
    object HelloScreen: Screen("helloScreen")
}
