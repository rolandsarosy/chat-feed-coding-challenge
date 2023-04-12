package com.rolandsarosy.chatfeedchallenge.common.extensions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator

fun NavController.navigateSafely(callerFragment: Fragment, @IdRes resId: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
    val currentDestination = (this.currentDestination as? FragmentNavigator.Destination)?.className
        ?: (this.currentDestination as? DialogFragmentNavigator.Destination)?.className
    if (currentDestination == callerFragment.javaClass.name || currentDestination == callerFragment.parentFragment?.javaClass?.name) {
        this.navigate(resId, args, navOptions)
    }
}

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T
