package com.example.tw_okta_and

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tw_okta_and.databinding.ActivityMainBinding
import com.example.tw_okta_and.ui.theme.TwOktaAndTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MyViewModel : ViewModel() {
    private val _message = MutableStateFlow("Hello World!")
    val message = _message.asStateFlow()

    private val _logs = MutableStateFlow("")
    val logs = _logs.asStateFlow()

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun addLog(log: String) {
        viewModelScope.launch {
            _logs.value += "\n$log"
        }
    }
}

class MainActivity : ComponentActivity() {
    // Firebase Analytics 인스턴스
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val TAG = "MainActivity"
    // View 바인딩 인스턴스
    private lateinit var binding: ActivityMainBinding

    // ViewModel 인스턴스
    private val viewModel: MyViewModel by viewModels()

    private companion object {
        const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩 초기화
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // ViewModel 바인딩
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Firebase 초기화
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Log.d(TAG, "onCreate called")
        viewModel.addLog("onCreate called")

        // "Starting log capture" 메시지 추가
        viewModel.updateMessage("Starting log capture")
        viewModel.addLog("Starting log capture")

        // 권한 요청 체크
        checkPermissions()

        // 접근성 서비스 설정 버튼 추가
        binding.accessibilityServiceButton.setOnClickListener {
            showAccessibilityDialog()
        }

        // Composable Content 설정
        setContent {
            TwOktaAndTheme {
                Log.d(TAG, "Setting content view")
                // 앱 컨텐츠 설정
                Greeting(viewModel)
            }
        }
        // Firebase 이벤트 로깅
        logFirebaseEvent("app_start", "Application started")
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE)
        } else {
            // 권한이 이미 허용된 경우 파일 경로 설정
            setupFilePaths()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되었습니다.
                setupFilePaths()
            } else {
                // 권한이 거부되었습니다.
                Log.d(TAG, "Permission denied")
                viewModel.addLog("Permission denied")
            }
        }
    }

    private fun setupFilePaths() {
        // 외부 저장소의 파일 경로 설정
        val externalFile = File(getExternalFilesDir(null), "yourfilename.txt")
        log("External File Path: ${externalFile.absolutePath}")

        // 내부 저장소의 파일 경로 설정
        val internalFile = File(filesDir, "yourfilename.txt")
        log("Internal File Path: ${internalFile.absolutePath}")

        // 내부 저장소의 데이터베이스 파일 경로 설정
        val internalDbFile = File(filesDir, "databases/yourdatabase.db")
        log("Internal DB File Path: ${internalDbFile.absolutePath}")

        // 외부 저장소의 데이터베이스 파일 경로 설정
        val externalDbFile = File(getExternalFilesDir(null), "databases/yourdatabase.db")
        log("External DB File Path: ${externalDbFile.absolutePath}")
    }

    private fun log(message: String) {
        Log.d(TAG, message)
        viewModel.addLog(message)
    }

    // Firebase 이벤트 로깅 메소드
    private fun logFirebaseEvent(event: String, description: String) {
        val bundle = Bundle().apply {
            putString("description", description)
        }
        firebaseAnalytics.logEvent(event, bundle)
        log("Logged Firebase event: $event - $description")
    }

    private fun showAccessibilityDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Accessibility Permission")
        builder.setMessage("This app requires accessibility permission to perform certain functions. Please enable accessibility service in settings.")
        builder.setPositiveButton("Go to Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        // 화면 뷰 이벤트 로깅
        logFirebaseEvent("screen_view", "MainActivity onResume")
        log("onResume called")
    }

    override fun onPause() {
        super.onPause()
        // 화면 뷰 이벤트 로깅
        logFirebaseEvent("screen_view", "MainActivity onPause")
        log("onPause called")
    }
}

// Composable 함수 정의
@Composable
fun Greeting(viewModel: MyViewModel) {
    Log.d("Greeting", "Greeting function called")
    val message = viewModel.message.collectAsState()
    Text(text = "Hello Android! ${message.value}")
}

// 미리보기 설정
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TwOktaAndTheme {
        Greeting(MyViewModel())
    }
}
