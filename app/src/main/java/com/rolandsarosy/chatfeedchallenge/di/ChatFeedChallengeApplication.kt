package com.rolandsarosy.chatfeedchallenge.di

import android.app.Application
import com.rolandsarosy.chatfeedchallenge.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class ChatFeedChallengeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ChatFeedChallengeApplication)
            androidFileProperties()
            modules(listOf(networkModule))
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // This can be used to instantiate a production-level logging utility, such as Bugsnag or Firebase Crashlytics.
            // "every time you log in production, a puppy dies." - Jake Wharton
        }
    }
}
