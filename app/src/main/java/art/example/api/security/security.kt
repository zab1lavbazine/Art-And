package art.example.api.security

import art.example.api.service.UserApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


//fun login(username: String, password: String, onSuccess: (String) -> Unit, onError: () -> Unit) {
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            // Replace this with your actual API call
//            val response = .(username, password)
//            if (response.isSuccessful && response.body() != null) {
//                val token = response.body()!!.token
//                // Save token to shared preferences or another storage solution
//                saveToken(token)
//                onSuccess(token) // Pass the token back to the success handler
//            } else {
//                onError() // Handle error (e.g., show a message to the user)
//            }
//        } catch (e: Exception) {
//            onError() // Handle exception
//        }
//    }
//}
//
//
//fun saveToken(token: String){
//
//}