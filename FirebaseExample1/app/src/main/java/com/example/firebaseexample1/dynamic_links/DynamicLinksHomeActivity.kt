package com.example.firebaseexample1.dynamic_links

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebaseexample1.databinding.ActivityDynamicLinksHomeBinding
import com.example.firebaseexample1.databinding.ActivityFireStoreHomeBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase

class DynamicLinksHomeActivity : AppCompatActivity() {

    private val TAG = DynamicLinksHomeActivity::class.java.simpleName

    private lateinit var binding: ActivityDynamicLinksHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicLinksHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        buttonClickListener()

        if (intent.data != null) {
            Log.i(TAG, "intent.data is not null")
            handleDeepLink()
        } else {
            Log.i(TAG, "intent.data is null")
        }
    }

    private fun buttonClickListener() {
        binding.btnCreateDeepLink.setOnClickListener {
            onDynamicLinkClick()
        }

        binding.btnCopyDeepLink.setOnClickListener {
            //클립보드 사용 코드
            val clipboardManager: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("ID", binding.tvDeepLink.getText().toString().trim()) //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
            clipboardManager.setPrimaryClip(clipData)

            //복사가 되었다면 토스트메시지 노출
            Toast.makeText(applicationContext, "텍스트가 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDeepLink(): Uri {
        val randomNumber = (0..100).random()
        Log.i(TAG, "랜덤 숫자: ${randomNumber}")
        return Uri.parse("https://www.testwebsite.com/" + "path1/path2" + "?id=${randomNumber}&pass=check")
    }

    private fun onDynamicLinkClick() {
        Firebase.dynamicLinks.shortLinkAsync {
            link = getDeepLink()
            domainUriPrefix = "https://firebaseexample1.page.link"
            androidParameters {
                minimumVersion = 1
                build()
            }
//            socialMetaTagParameters {
//                title = binding.tvTitle.text.toString()
//                description = binding.tvContents.text.toString()
//                imageUrl = Uri.parse(sdThumbnail)
//                build()
//            }
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                val shortLink: Uri? = it.result.shortLink
                Log.i(TAG, "딥링크: ${shortLink}")
                binding.tvDeepLink.text = shortLink.toString()

//                try {
//                    val sendIntent = Intent()
//                    sendIntent.action = Intent.ACTION_SEND
//                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "사운드두들로 만든 이야기가 도착했어요! 더 많은 이야기는 사운드두들에서 확인해보세요.\n")
//                    sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
////                    sendIntent.putExtra(Intent.EXTRA_TITLE, title)
//                    sendIntent.type = "text/plain"
//                    startActivity(Intent.createChooser(sendIntent, "Share"))
//                } catch (ignored: ActivityNotFoundException) {
//                    Log.i(TAG, "Exception ${ignored.message}")
//                }
            } else {
                Log.w(TAG, "isNotSuccessful: ${it.toString()}")
//                showToast(getString(R.string.common_retry))
            }
        }.addOnFailureListener {
            Log.w(TAG, "addOnFailureListener: ${it.toString()}")
//            showToast(getString(R.string.common_retry))
        }
    }


    private fun handleDeepLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this, OnSuccessListener { pendingDynamicLinkData ->
                if (pendingDynamicLinkData == null) {
                    Log.d(TAG, "No have dynamic link")
                    return@OnSuccessListener
                }
                val deepLink = pendingDynamicLinkData.link
                Log.d(TAG, "deepLink: $deepLink")
                val segment = deepLink!!.pathSegments
                Log.i(TAG, "segment: ${segment}")
                Log.i(TAG, "id: " + deepLink.getQueryParameter("id"))
                Log.i(TAG, "path: ${deepLink.encodedPath}\n${deepLink.query}\n${deepLink.scheme}\n${deepLink.lastPathSegment}\n${deepLink.pathSegments[0]}\n${deepLink.path}\n" +
                        "${pendingDynamicLinkData.utmParameters}\n${pendingDynamicLinkData.redirectUrl}\n${pendingDynamicLinkData.extensions}\n")

                binding.tvDeepLink.text = deepLink.getQueryParameter("id") + " / " + deepLink.getQueryParameter("pass")

                val intent = Intent(binding.root.context, DynamicLinksValuesActivity::class.java)
                intent.putExtra("ID", deepLink.getQueryParameter("id")?.toInt())
                intent.putExtra("PASS", deepLink.getQueryParameter("pass").toString())
                startActivity(intent)

                Log.i(TAG, "딥링크 종료")
                finish()


//                    deepLink.toString().apply {
//                        if(this.contains(URL.DEEPLINK_SD_CONTENT)) {
//                            Log.i("DeepLink", "아이디 파라미터 ${deepLink.getQueryParameter("id")}")
//                            RequestPage.getSounddoodleInfo(mActivity, deepLink.getQueryParameter("id"))
//                        } else if(this.contains(URL.DEEPLINK_D_CONTENT)) {
//                            RequestPage.getDoodleInfo(mActivity, deepLink.getQueryParameter("id"))
//                        }
//                    }
            })
            .addOnFailureListener(this) { e ->
                Log.w(TAG, "getDynamicLink:onFailure", e)
            }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.data != null) {
            Log.i(TAG, "intent.data is not null in onNewIntent()")
            handleDeepLink()
        } else {
            Log.i(TAG, "intent.data is null in onNewIntent()")
        }
    }
}