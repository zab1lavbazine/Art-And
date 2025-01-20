package art.example.modules

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

class LocalDateTimeAdapter {
    @ToJson
    fun toJson(dateTime: LocalDateTime): String = dateTime.toString()

    @FromJson
    fun fromJson(dateTimeString: String): LocalDateTime = dateTimeString.toLocalDateTime()
}