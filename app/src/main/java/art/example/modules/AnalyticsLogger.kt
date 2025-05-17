package art.example.modules

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics



object AnalyticsLogger {
    fun logEvent(eventName: String, params : Map<String, String> = emptyMap()){
        val analytics = Firebase.analytics
        val bundle = Bundle()
        params.forEach{
            (key , value) -> bundle.putString(key, value)
        }
        analytics.logEvent(eventName, bundle)
    }
}