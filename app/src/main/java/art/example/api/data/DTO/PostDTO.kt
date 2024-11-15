package art.example.api.data.DTO

import okhttp3.MultipartBody

data class PostDTO(
    var title: String? = null,
    var description: String? = null,
    var tagsId: List<Long> = emptyList(),
    var file: MultipartBody.Part? = null
)
