package com.dygames.sample

import android.content.Context
import com.dygames.roompager.Adapter

class RoomPagerAdapter(
    private var videoUrls: List<String> = emptyList(), private val loadNextRoom: () -> List<String>
) : Adapter<SampleVideoPlayer> {

    private var viewHolders: List<SampleVideoPlayer> = emptyList()
    private var lastCurrentRoomPosition = 0

    override fun createViewHolder(context: Context): SampleVideoPlayer = SampleVideoPlayer(context)

    override fun getItemCount(): Int = videoUrls.size

    override fun onRecycle(
        currentRoomPosition: Int, recycledViewHolders: List<Adapter.ViewHolder>
    ) {
        viewHolders = recycledViewHolders.map { it as SampleVideoPlayer }
        lastCurrentRoomPosition = currentRoomPosition
        navigateRooms(currentRoomPosition)
        play()
    }

    override fun onLoadNextRoom() {
        setData(loadNextRoom())
    }

    private fun setData(data: List<String>) {
        videoUrls = data
        navigateRooms(lastCurrentRoomPosition)
    }

    fun play() {
        viewHolders.forEachIndexed { index, item ->
            if (index == viewHolders.size / 2) {
                item.play()
            } else {
                item.pause()
            }
        }
    }

    fun pause() {
        viewHolders.forEach {
            it.pause()
        }
    }

    private fun navigateRooms(currentRoomPosition: Int) {
        navigateRoom(-1, currentRoomPosition)
        navigateRoom(0, currentRoomPosition)
        navigateRoom(1, currentRoomPosition)
    }

    private fun navigateRoom(position: Int, currentRoomPosition: Int) {
        val center = 1
        val target = repeat(center + position, 3)
        if (target >= viewHolders.size) {
            return
        }
        val room = viewHolders[target]
        if (videoUrls.size > currentRoomPosition + position && currentRoomPosition + position >= 0) {
            room.navigate(videoUrls[currentRoomPosition + position])
        }
    }

    private fun repeat(n: Int, max: Int) = if (n >= max) {
        max % n
    } else if (n < 0) {
        (max - n) % n
    } else {
        n
    }
}
