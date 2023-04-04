package com.example.firebasedynamiclink1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebasedynamiclink1.databinding.ActivityAd1Binding
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class AD1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAd1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAd1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAd1.setOnClickListener {
            onDynamicLinkClick()
        }

    }

    private fun getPromotionDeepLink(): Uri {
        val flag = AD1_FLAG
        val code = "Ad1"
        val publisher = "marketing1"
//        https://your_subdomain.page.link/?link=your_deep_link&apn=package_name[&amv=minimum_version][&afl=fallback_link]
//        return Uri.parse("https://testeditlink.page.link/?link=https://search.naver.com/search.naver/${flag}?query=광고1&code=${code}&publisher=${publisher}")
//        return Uri.parse("https://testeditlink.page.link/?" +
//                "link=https://search.naver.com/search.naver/${flag}&where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=광고1&code=${code}&publisher=${publisher}" +
//                "&apn=${packageName}&amv=1&afl=https://www.naver.com")
//        link=https://edit.com/${flag}&where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=광고1&code=${code}&publisher=${publisher}&apn=com.example.firebasedynamiclink1&afl=https://www.naver.com
//            return Uri.parse("https://search.naver.com/search.naver/${flag}?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=광고1&code=${code}&publisher=${publisher}")
        return Uri.parse("https://edit.com/${flag}?code=${code}&publisher=${publisher}")
    }

    private fun onDynamicLinkClick() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLongLink(getPromotionDeepLink())
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
                    .setTitle("AD1 Example of a Dynamic Link")
                    .setDescription("This link works whether the app is installed or not!")
                    .setImageUrl(Uri.parse("https://images.unsplash.com/photo-1667114790658-0afc31212d8e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwxfDB8MXxyYW5kb218MHx8fHx8fHx8MTY3MDMzNzE5NA&ixlib=rb-4.0.3&q=80&w=1080"))
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
                    Log.w("MYTAG", task.exception.toString())
                }
            }
    }
}