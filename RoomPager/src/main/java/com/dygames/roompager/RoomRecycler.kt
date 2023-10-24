package com.dygames.roompager

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import com.dygames.roompager.scrollpager.ScrollPager

@SuppressLint("ViewConstructor")
class RoomRecycler(
    context: Context, private val gridSize: Int
) : GridLayout(context) {

    private lateinit var adapter: Adapter<Adapter.ViewHolder>
    private val viewHolders: List<Adapter.ViewHolder> by lazy {
        (0 until gridSize).map {
            adapter.createViewHolder(context)
        }
    }
    private val center = (gridSize * gridSize) / 2

    fun setAdapter(adapter: Adapter<Adapter.ViewHolder>) {
        this.adapter = adapter

        initLayout()
        initContentView()
    }

    fun recyclePrevRooms(scrollPager: ScrollPager, currentRoomPosition: Int) {
        val start = scrollPager.calculateStartChildPosition(gridSize)
        val end = scrollPager.calculateEndChildPosition(gridSize)

        val positions = listOf(start, center, end)
        val rooms = listOf(getChildAt(end), getChildAt(start), getChildAt(center))

        recycleRoomByPosition(scrollPager, currentRoomPosition, positions, rooms)
    }

    fun recycleNextRooms(scrollPager: ScrollPager, currentRoomPosition: Int) {
        val start = scrollPager.calculateStartChildPosition(gridSize)
        val end = scrollPager.calculateEndChildPosition(gridSize)

        val positions = listOf(start, center, end)
        val rooms = listOf(getChildAt(center), getChildAt(end), getChildAt(start))

        recycleRoomByPosition(scrollPager, currentRoomPosition, positions, rooms)
    }

    private fun recycleRoomByPosition(
        scrollPager: ScrollPager, currentRoomPosition: Int, positions: List<Int>, rooms: List<View>
    ) {
        rooms.forEach { removeView(it) }
        rooms.forEachIndexed { index, view -> addView(view, positions[index]) }

        adapter.onRecycle(scrollPager.pagingOrientation,
            currentRoomPosition,
            rooms.map { it as Adapter.ViewHolder })
    }

    fun navigate(scrollPager: ScrollPager, currentRoomPosition: Int) {
        val start = scrollPager.calculateStartChildPosition(gridSize)
        val end = scrollPager.calculateEndChildPosition(gridSize)
        val rooms = listOf(getChildAt(start), getChildAt(center), getChildAt(end))

        adapter.onRecycle(
            PagingOrientation.BOTH,
            currentRoomPosition,
            rooms.map { it as Adapter.ViewHolder })
    }

    fun swapOrientation() {
        val verticalSpace = gridSize
        val horizontalSpace = 1
        swapView(center - verticalSpace, center - horizontalSpace)
        swapView(center + horizontalSpace, center + verticalSpace)
    }

    fun roomSize(): Int = adapter.getItemCount()

    fun loadMore() {
        adapter.onLoadNextRoom()
    }

    private fun swapView(start: Int, end: Int) {
        val first = getChildAt(start)
        val second = getChildAt(end)
        removeView(first)
        removeView(second)
        addView(second, start)
        addView(first, end)
    }

    private fun initLayout() {
        columnCount = gridSize
        rowCount = gridSize
        layoutParams = LinearLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun initContentView() {
        (0 until gridSize * gridSize).forEach {
            val view = if (it % gridSize == gridSize / 2) {
                (viewHolders[it / gridSize] as View)
            } else {
                View(context)
            }
            view.layoutParams = LinearLayout.LayoutParams(
                resources.displayMetrics.widthPixels, RoomScreen.getScreenHeight(context)
            )
            addView(view)
        }
        adapter.onRecycle(PagingOrientation.BOTH, 0, viewHolders)
    }

}
