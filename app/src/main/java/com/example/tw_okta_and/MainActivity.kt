package com.example.tw_okta_and

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tw_okta_and.ui.theme.TwOktaAndTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.io.File
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // Firebase Analytics 인스턴스
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val TAG = "MainActivity"

    private lateinit var logTextView: TextView

    // ViewModel 인스턴스
    private val viewModel: MyViewModel by viewModels()

    companion object {
        private const val REQUEST_CODE_WRITE_SETTINGS = 101
        private const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logTextView = findViewById(R.id.logTextView)
        val textView = findViewById<TextView>(R.id.textView)

        lifecycleScope.launch {
            viewModel.message.collect { message ->
                textView.text = message
            }
        }

        lifecycleScope.launch {
            viewModel.logs.collect { logs ->
                logTextView.text = logs
            }
        }

        // 화면 밝기 설정
        if (Settings.System.canWrite(this)) {
            setBrightness(1.0f)
        } else {
            requestWriteSettingsPermission()
        }

        // Firebase 초기화
        firebaseAnalytics = Firebase.analytics
        Log.d(TAG, "onCreate called")
        viewModel.addLog("onCreate called")

        // "Starting log capture" 메시지 추가
        viewModel.updateMessage("Starting log capture")
        viewModel.addLog("Starting log capture")

        // 권한 요청 체크
        checkPermissions()

        // Composable Content 설정
        setContent {
            TwOktaAndTheme {
                Log.d(TAG, "Setting content view")
                // 앱 컨텐츠 설정
                MainScreen(viewModel)
            }
        }

        // Firebase 이벤트 로깅
        logFirebaseEvent("app_start", "Application started")
    }

    // 밝기를 설정하는 메서드
    private fun setBrightness(brightness: Float) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness
        window.attributes = layoutParams
    }

    private fun requestWriteSettingsPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                setBrightness(1.0f)
            } else {
                Toast.makeText(this, "Write settings permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        } else {
            setupFilePaths()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupFilePaths()
            } else {
                log("Permission denied")
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
        logTextView.append("\n$message")
    }

    // Firebase 이벤트 로깅 메소드
    private fun logFirebaseEvent(event: String, description: String) {
        val bundle = Bundle().apply {
            putString("description", description)
        }
        firebaseAnalytics.logEvent(event, bundle)
        log("Logged Firebase event: $event - $description")
    }

    override fun onResume() {
        super.onResume()
        // 접근성 서비스 활성화 상태 확인
        val isAccessibilityServiceEnabled = isAccessibilityServiceEnabled()
        if (!isAccessibilityServiceEnabled) {
            showAccessibilityDialog()
        } else {
            // 접근성 권한이 활성화된 경우에만 Okta 인증 처리
            handleOktaAuthentication()
        }
        // 화면 뷰 이벤트 로깅
        logFirebaseEvent("screen_view", "MainActivity onResume")
        log("onResume called")
    }

    private fun handleOktaAuthentication() {
        // Okta 인증 처리 로직 추가
        // AccessibilityService에서 Okta 인증 화면을 감지하고 자동으로 승인하는 코드 호출
        YourAccessibilityService.startOktaAuthentication(this)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val prefString = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return prefString?.contains(packageName + "/" + YourAccessibilityService::class.java.name) ?: false
    }

    private fun showAccessibilityDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Accessibility Permission")
        builder.setMessage("This app requires accessibility permission to perform certain functions. Please enable accessibility service in settings.")
        builder.setPositiveButton("Go to Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onPause() {
        super.onPause()
        // 화면 뷰 이벤트 로깅
        logFirebaseEvent("screen_view", "MainActivity onPause")
        log("onPause called")
    }
}

@Composable
fun MainScreen(viewModel: MyViewModel) {
    val context = LocalContext.current
    val message = viewModel.message.collectAsState()
    val logs = viewModel.logs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Message: ${message.value}")
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(logs.value.split("\n")) { log ->
                Text(text = log)
            }
        }
    }
}