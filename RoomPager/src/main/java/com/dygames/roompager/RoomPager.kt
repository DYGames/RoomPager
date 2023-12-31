package com.dygames.roompager

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.dygames.roompager.scrollpager.HorizontalScrollPager
import com.dygames.roompager.scrollpager.ScrollPager
import com.dygames.roompager.scrollpager.VerticalScrollPager

class RoomPager(
    context: Context, attributeSet: AttributeSet
) : FrameLayout(context, attributeSet) {

    private val pageThreshold: Float
    private val gridSize: Int
    private val isNextRoomLoadable: Boolean
    private var currentRoomPosition: Int = 0
    private var lastPagingOrientation: PagingOrientation = PagingOrientation.VERTICAL

    private val verticalScrollPager: VerticalScrollPager = VerticalScrollPager(context)
    private val horizontalScrollPager: HorizontalScrollPager = HorizontalScrollPager(context)
    private val roomRecycler: RoomRecycler

    init {
        val attrs = context.theme.obtainStyledAttributes(
            attributeSet, R.styleable.RoomPagerStyle, 0, 0
        )

        currentRoomPosition = attrs.getInteger(R.styleable.RoomPagerStyle_roomPosition, 0)
        isNextRoomLoadable = attrs.getBoolean(R.styleable.RoomPagerStyle_isNextRoomLoadable, true)
        pageThreshold = attrs.getFloat(R.styleable.RoomPagerStyle_pageThreshold, 10.0f)
        gridSize = attrs.getInteger(R.styleable.RoomPagerStyle_gridSize, 3)
        setOrientation(
            PagingOrientation.values()[attrs.getInt(
                R.styleable.RoomPagerStyle_orientation, 2
            )]
        )

        roomRecycler = RoomRecycler(context, gridSize)
    }

    fun setAdapter(adapter: Adapter<Adapter.ViewHolder>) {
        roomRecycler.setAdapter(adapter)

        initVerticalScrollView()
        initHorizontalScrollView()
        initOrientation()

        roomRecycler.navigate(verticalScrollPager, currentRoomPosition)
    }

    fun setOrientation(pagingOrientation: PagingOrientation) {
        horizontalScrollPager.isScrollable =
            pagingOrientation == PagingOrientation.BOTH || pagingOrientation == PagingOrientation.HORIZONTAL
        verticalScrollPager.isScrollable =
            pagingOrientation == PagingOrientation.BOTH || pagingOrientation == PagingOrientation.VERTICAL
    }

    fun getRoomLoadable() = isNextRoomLoadable

    private fun initOrientation() {
        post {
            horizontalScrollPager.scrollTo(
                horizontalScrollPager.scrollPosition * horizontalScrollPager.screenSize
            )
            verticalScrollPager.scrollTo(
                verticalScrollPager.scrollPosition * verticalScrollPager.screenSize
            )
            lastPagingOrientation = verticalScrollPager.pagingOrientation
        }
    }

    private fun initVerticalScrollView() {
        initScrollPager(verticalScrollPager)
        isVerticalScrollBarEnabled = false
        verticalScrollPager.scrollPosition = gridSize / 2
        verticalScrollPager.layoutParams = LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )

        addView(verticalScrollPager)
    }

    private fun initHorizontalScrollView() {
        initScrollPager(horizontalScrollPager)
        horizontalScrollPager.scrollPosition = gridSize / 2
        horizontalScrollPager.layoutParams = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        )
        verticalScrollPager.addView(horizontalScrollPager)
        horizontalScrollPager.addView(roomRecycler)
    }

    private fun initScrollPager(scrollPager: ScrollPager) {
        initScrollMotionEvent(scrollPager)
        initScrollEndEvent(scrollPager)
    }

    private fun initScrollMotionEvent(scrollPager: ScrollPager) {
        val pagingThreshold = (scrollPager.screenSize / pageThreshold)
        scrollPager.setOnScrollChangeListener { scroll ->
            if (lastPagingOrientation != scrollPager.pagingOrientation) {
                lastPagingOrientation = scrollPager.pagingOrientation
                roomRecycler.swapOrientation()
            }
            val topPosition = scrollPager.scrollPosition * scrollPager.screenSize
            scrollPager.pagingState = if (scroll < topPosition - pagingThreshold) {
                PagingState.PREVIOUS
            } else if (scroll > topPosition + pagingThreshold) {
                PagingState.NEXT
            } else {
                PagingState.CURRENT
            }
        }
    }

    private fun initScrollEndEvent(scrollPager: ScrollPager) {
        scrollPager.setOnTouchListener {
            determinePositions(scrollPager)
            recycleRooms(scrollPager)
            pageToTargetRoom(scrollPager)
        }
    }

    private fun determinePositions(scrollPager: ScrollPager) {
        when (scrollPager.pagingState) {
            PagingState.PREVIOUS -> {
                currentRoomPosition--
                scrollPager.scrollPosition--
            }

            PagingState.CURRENT -> Unit
            PagingState.NEXT -> {
                currentRoomPosition++
                scrollPager.scrollPosition++
            }
        }
    }

    private fun recycleRooms(scrollPager: ScrollPager) {
        if (currentRoomPosition == roomRecycler.roomSize() - 2 && isNextRoomLoadable) {
            roomRecycler.loadMore()
        }
        if (currentRoomPosition < 0) {
            scrollPager.scrollPosition = 1
            currentRoomPosition = 0
            roomRecycler.navigate(scrollPager, currentRoomPosition)
        } else if (currentRoomPosition >= roomRecycler.roomSize() && !isNextRoomLoadable) {
            scrollPager.scrollPosition = 1
            currentRoomPosition = roomRecycler.roomSize() - 1
            roomRecycler.navigate(scrollPager, currentRoomPosition)
        } else if (scrollPager.scrollPosition <= 0) {
            roomRecycler.recyclePrevRooms(scrollPager, currentRoomPosition)
            scrollPager.scrollPosition++
            scrollPager.scrollBy(scrollPager.screenSize)
        } else if (scrollPager.scrollPosition >= gridSize - 1) {
            roomRecycler.recycleNextRooms(scrollPager, currentRoomPosition)
            scrollPager.scrollPosition--
            scrollPager.scrollBy(-scrollPager.screenSize)
        }
    }

    private fun pageToTargetRoom(scrollPager: ScrollPager) {
        scrollPager.post {
            scrollPager.smoothScrollTo(
                scrollPager.scrollPosition * scrollPager.screenSize
            )
        }
    }
}
