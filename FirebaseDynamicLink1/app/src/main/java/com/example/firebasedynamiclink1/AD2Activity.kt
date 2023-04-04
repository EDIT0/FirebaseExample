package com.example.firebasedynamiclink1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebasedynamiclink1.databinding.ActivityAd2Binding
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class AD2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAd2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAd2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAd2.setOnClickListener {
            onDynamicLinkClick()
        }

    }

    private fun getPromotionDeepLink(): Uri {
        val flag = AD2_FLAG
        val code = "Ad2"
        val publisher = "marketing2"
        return Uri.parse("https://search.naver.com/search.naver/${flag}?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=광고2&code=${code}&publisher=${publisher}")
    }

    private fun onDynamicLinkClick() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(getPromotionDeepLink())
            .setDomainUriPrefix("https://testeditlink.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder(packageName)
                    .setMinimumVersion(1)
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
                    .setTitle("AD2 Example of a Dynamic Link")
                    .setDescription("This link works whether the app is installed or not!")
                    .setImageUrl(Uri.parse("https://images.unsplash.com/photo-1669061935095-36c69c5fc387?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxfDB8MXxyYW5kb218MHx8fHx8fHx8MTY3MDMzNzExMQ&ixlib=rb-4.0.3&q=80&w=1080"))
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