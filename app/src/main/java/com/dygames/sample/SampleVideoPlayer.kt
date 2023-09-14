package com.dygames.sample

import android.content.Context
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import com.dygames.roompager.Adapter


class SampleVideoPlayer(context: Context) : VideoView(context),
    Adapter.ViewHolder {

    private var currentVideoUrl: String = ""

    init {
        val mediaController = MediaController(context)
        mediaController.setAnchorView(this)
        mediaController.setMediaPlayer(this)
        setOnPreparedListener { it.isLooping = true }
        setMediaController(mediaController)
    }

    fun play() {
        if (!isPlaying)
            start()
    }

    override fun pause() {

    }

    fun navigate(videoUrl: String) {
        if (currentVideoUrl == videoUrl)
            return
        val uri = Uri.parse(videoUrl)
        setVideoURI(uri)
        currentVideoUrl = videoUrl
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        )
    }
}