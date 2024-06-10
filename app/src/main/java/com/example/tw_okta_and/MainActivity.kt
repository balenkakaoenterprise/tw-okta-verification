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
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.example.tw_okta_and.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        binding.textView.text = "Hello World!"

        // Firebase 초기화
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Log.d(TAG, "onCreate called")

        setContent {
            TwOktaAndTheme {
                Log.d(TAG, "Setting content view")
                // Your app content
                Greeting("Android")
            }
        }
        logFirebaseEvent("app_start", "Application started")
    }
    private fun logFirebaseEvent(event: String, description: String) {
        val bundle = Bundle()
        bundle.putString("description", description)
        firebaseAnalytics.logEvent(event, bundle)
    }
    override fun onResume() {
        super.onResume()
        logFirebaseEvent("screen_view", "MainActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        logFirebaseEvent("screen_view", "MainActivity onPause")
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
