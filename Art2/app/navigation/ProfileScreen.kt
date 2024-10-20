package com.example.arthub.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ProfileScreen(userId: Long, onBack : () -> Unit){
    Text("Other profiles")



    Button(onClick = onBack){
        Text("Back")
    }
}


@Composable
fun MyProfile(onBack: () -> Unit){
    Text("My profile")


    Button(onClick = onBack){
        Text("Back")
    }
}