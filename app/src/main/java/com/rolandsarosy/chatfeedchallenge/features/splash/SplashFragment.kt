package com.rolandsarosy.chatfeedchallenge.features.splash

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rolandsarosy.chatfeedchallenge.R
import com.rolandsarosy.chatfeedchallenge.common.base.BaseFragment
import com.rolandsarosy.chatfeedchallenge.common.extensions.navigateSafely
import com.rolandsarosy.chatfeedchallenge.databinding.FragmentSplashBinding

class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    companion object {
        private const val DELAY_TIME = 750L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Disgusting placeholder implementation. Will be replaced by the Jetpack SplashScreen API.
        Handler().postDelayed({ findNavController().navigateSafely(this, R.id.action_splashFragment_to_chatFragment) }, DELAY_TIME)
    }
}
