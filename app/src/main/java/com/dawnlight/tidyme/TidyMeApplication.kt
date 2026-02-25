package com.dawnlight.tidyme

import android.app.Application
import android.content.pm.ApplicationInfo
import com.dawnlight.tidyme.data.firebase.FirebaseManager
import com.google.firebase.firestore.FirebaseFirestore

class TidyMeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseManager.initialize(this)

        // Enable debug logging in debug builds
        //  if (BuildConfig.DEBUG) {
        if((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            FirebaseFirestore.setLoggingEnabled(true)
        }
    }
}
