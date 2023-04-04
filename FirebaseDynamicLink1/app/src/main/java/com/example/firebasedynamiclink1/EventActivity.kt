package com.example.firebasedynamiclink1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebasedynamiclink1.databinding.ActivityEventBinding
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class EventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnShareEvent.setOnClickListener {
            onDynamicLinkClick()
        }
    }

    private fun getPromotionDeepLink(): Uri {
        val flag = EVENT_FLAG
        val code = "Android"
        return Uri.parse("https://search.naver.com/search.naver/${flag}?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=이벤트&code=${code}")
    }

    private fun onDynamicLinkClick() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(getPromotionDeepLink())
            .setDomainUriPrefix("https://testeditlink.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder(packageName)
                    .setMinimumVersion(6) // 앱 버전 정보 (현재 앱 버전 5로 설정해놓음)
                    .build()
            )
//            .setGoogleAnalyticsParameters(
//                Builder()
//                    .setSource("orkut")
//                    .setMedium("social")
//                    .setCampaign("example-promo")
//                    .build()
//            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle("Event Example of a Dynamic Link")
                    .setDescription("This link works whether the app is installed or not!")
                    .setImageUrl(Uri.parse("https://images.unsplash.com/photo-1668714298639-2df174929fcb?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxfDB8MXxyYW5kb218MHx8fHx8fHx8MTY3MDMzNzE2Mw&ixlib=rb-4.0.3&q=80&w=1080"))
                    .build()
            )
            .buildShortDynamicLink()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shortLink: Uri = task.result.getShortLink()!!
                    try {
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                        sendIntent.type = "text/plain"
                        startActivity(Intent.createChooser(sendIntent, "Share"))
                    } catch (ignored: ActivityNotFoundException) {
                    }
                } else {
                    Log.w("MYTAG", task.toString())
                }
            }
    }
}