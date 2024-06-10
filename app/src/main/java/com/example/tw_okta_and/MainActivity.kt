package com.example.tw_okta_and

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tw_okta_and.ui.theme.TwOktaAndTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 초기화
        FirebaseApp.initializeApp(this)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Log.d(TAG, "onCreate called")

        setContent {
            TwOktaAndTheme {
                Log.d(TAG, "Setting content view")
                // Your app content
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Log.d("Greeting", "Greeting function called with name: $name")
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TwOktaAndTheme {
        Greeting("Android")
    }
}
