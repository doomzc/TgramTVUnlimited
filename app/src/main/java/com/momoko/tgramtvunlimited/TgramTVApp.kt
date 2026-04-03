package com.momoko.tgramtvunlimited

import android.app.Application
import com.momoko.tgramtvunlimited.data.api.TelegramApiClient

class TgramTVApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize TDLib
        TelegramApiClient.getInstance(this).initialize()
    }
}
