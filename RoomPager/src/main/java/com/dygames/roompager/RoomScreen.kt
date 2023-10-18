package com.dygames.roompager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.WindowManager
import androidx.core.view.WindowInsetsCompat

object RoomScreen {
    fun getScreenHeight(context: Context): Int {
        val windowManager = (context as? Activity)?.windowManager ?: return 0
        val realHeight = getRealHeight(windowManager)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            realHeight - getNavigationBarHeight(windowManager)
        } else {
            realHeight - getNavHeight()
        }
    }

    private fun getNavigationBarHeight(windowManager: WindowManager): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.windowInsets.getInsets(
                WindowInsetsCompat.Type.navigationBars()
            ).bottom
        } else {
            0
        }
    }

    private fun getRealHeight(windowManager: WindowManager): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            val displayMetrics = Resources.getSystem().displayMetrics.apply {
                windowManager.defaultDisplay.getRealMetrics(this)
            }
            displayMetrics.heightPixels
        }
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun getNavHeight(): Int {
        val resources = Resources.getSystem()
        val navHeight = resources.getIdentifier(
            "navigation_bar_height", "dimen", "android"
        )

        return if (navHeight > 0) {
            resources.getDimensionPixelSize(navHeight)
        } else {
            0
        }
    }

}
