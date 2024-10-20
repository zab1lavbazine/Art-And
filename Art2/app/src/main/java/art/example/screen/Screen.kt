package art.example.screen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object PostsScreen : Screen("posts")
    object MyProfile : Screen("profile")
    object PostDetail : Screen("postDetail/{postId}") { // Ensure this is correct
        fun createRoute(postId: Long) = "postDetail/$postId"
    }
}
