package com.rolandsarosy.chatfeedchallenge.common.base

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.Observer
import com.rolandsarosy.chatfeedchallenge.common.event.Event

interface ErrorObserver {
    // TODO - TECH DEBT - This is a placeholder error showcase until a generic method is developed.
    fun defaultErrorObserver(context: Context): Observer<Event<String>> = Observer { errorEvent ->
        errorEvent.getContentIfNotHandled()?.let { errorMessage -> Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show() }
    }
}
