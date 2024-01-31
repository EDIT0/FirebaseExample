package com.example.firebaseexample1.remote_config

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebaseexample1.R
import com.example.firebaseexample1.databinding.ActivityRemoteConfigHomeBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONObject

class RemoteConfigHomeActivity : AppCompatActivity() {

    private val TAG = RemoteConfigHomeActivity::class.java.simpleName + " MYTAG "

    private lateinit var binding: ActivityRemoteConfigHomeBinding

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemoteConfigHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val configSettings = remoteConfigSettings {
            // minimumFetchIntervalInSeconds는 설정해두면 마지막으로 패치된 이후로 그 시간이 지나기 전까지는 패치(변경)되지 않는다.
            // 앱을 나갔다 와도 새로운 데이터로 패치되지 않으니 잘 설정해야 한다.
            // 디폴트 값은 12시간
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config)
        }

        Log.d(TAG, "${remoteConfig.hashCode()}")

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    Log.d(TAG, "addOnCompleteListener: ${task.result}")
                    val android = remoteConfig.getString("android")
                    Log.d(TAG, "fetchAndActivate: ${android}")

                    val jsonObject = JSONObject(android)
                    val intro = jsonObject.getJSONObject("intro")

                    val intro_server = intro.getJSONObject("server")
                    val intro_server_maintenance = intro_server.getBoolean("maintenance")
                    val intro_server_title = intro_server.getString("title")
                    val intro_server_content = intro_server.getString("content")

                    val intro_appUpdate = intro.getJSONObject("appUpdate")
                    val intro_appUpdate_versionCode = intro_appUpdate.getInt("versionCode")
                    val intro_appUpdate_isMandatory = intro_appUpdate.getBoolean("isMandatory")
                    val intro_appUpdate_title = intro_appUpdate.getString("title")
                    val intro_appUpdate_content = intro_appUpdate.getString("content")

                    val intro_notice = intro.getJSONObject("notice")
                    val intro_notice_title = intro_notice.getString("title")
                    val intro_notice_content = intro_notice.getString("content")

                    Log.d(TAG, "intro_server_maintenance: ${intro_server_maintenance}\n" +
                            "intro_server_title: ${intro_server_title}\n" +
                            "intro_server_content: ${intro_server_content}\n" +
                            "intro_appUpdate_versionCode: ${intro_appUpdate_versionCode}\n" +
                            "intro_appUpdate_isMandatory: ${intro_appUpdate_isMandatory}\n" +
                            "intro_appUpdate_title: ${intro_appUpdate_title}\n" +
                            "intro_appUpdate_content: ${intro_appUpdate_content}\n" +
                            "intro_notice_title: ${intro_notice_title}\n" +
                            "intro_notice_content: ${intro_notice_content}\n")
                } else {
                    Log.d(TAG, "${task.exception}")
                }
            }
            .addOnCanceledListener {
                Log.d(TAG, "addOnCanceledListener()")
            }
            .addOnFailureListener {
                Log.d(TAG, "addOnFailureListener() ${it.message}")
            }

        val android = remoteConfig.getString("android")
        Log.d(TAG, "normal: ${android}")

    }
}