package com.dygames.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dygames.roompager.RoomPager

class MainActivity : AppCompatActivity() {
    private val adapter: RoomPagerAdapter by lazy {
        val videoUrls = listOf(
            "http://techslides.com/demos/sample-videos/small.mp4",
            "https://media.w3.org/2010/05/sintel/trailer.mp4",
            "https://va.media.tumblr.com/tumblr_o600t8hzf51qcbnq0_480.mp4",
            "https://media.w3.org/2010/05/sintel/trailer.mp4",
            "http://techslides.com/demos/sample-videos/small.mp4",
            "https://va.media.tumblr.com/tumblr_o600t8hzf51qcbnq0_480.mp4",
            "https://media.w3.org/2010/05/sintel/trailer.mp4",
            "http://techslides.com/demos/sample-videos/small.mp4",
            "https://media.w3.org/2010/05/sintel/trailer.mp4",
            "https://va.media.tumblr.com/tumblr_o600t8hzf51qcbnq0_480.mp4",
        )
        RoomPagerAdapter(videoUrls) { videoUrls }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RoomPager>(R.id.main_roomPager).setAdapter(adapter)
    }

    override fun onResume() {
        super.onResume()
        adapter.play()
    }

    override fun onPause() {
        super.onPause()
        adapter.pause()
    }
}
