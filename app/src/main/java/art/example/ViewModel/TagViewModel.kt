package art.example.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import art.example.api.data.Tag
import art.example.api.repository.impl.TagRepository
import kotlinx.coroutines.launch

class TagViewModel(
    private val tagRepository: TagRepository,
    private val context: Context
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage


    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> get() = _tags


        fun loadTags(){
        viewModelScope.launch {
            try {
                val fetchedTags = tagRepository.loadTags() // Fetch tags from your repository
                _tags.value = fetchedTags
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}