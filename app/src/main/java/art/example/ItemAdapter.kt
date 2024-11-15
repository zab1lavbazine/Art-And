import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import art.example.api.data.Post

class PostAdapter(private val onClick: (Long) -> Unit) :
    PagingDataAdapter<Post, PostAdapter.PostViewHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        // Use a built-in layout like simple_list_item_2 for simplicity
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false) // Built-in Android layout
        return PostViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        if (post != null) {
            holder.bind(post)
        }
    }

    // ViewHolder to reference the views without data binding
    class PostViewHolder(itemView: View, private val onClick: (Long) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(android.R.id.text1)
        private val bodyTextView: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(post: Post) {
            // Set text in the views
            titleTextView.text = post.title
            bodyTextView.text = post.description

            // Set click listener
            itemView.setOnClickListener { onClick(post.id) }
        }
    }

    companion object {
        private val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
        }
    }
}
