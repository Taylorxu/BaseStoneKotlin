package com.xuzhiguang.xzglibrary.view.recycleViewExtension

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.xuzhiguang.xzglibrary.R

/**
 * Created by 徐志广 on 2018/4/28.
 * recycleView addItemDecoration 自定义的分割线 由于 Rview 是第三方
 * 需要  for (i in 2 until childCount!! - 2) 不给第一个头部刷新view 和底部状态view 话分割线
 */
object ItemDecorationEx {
    fun LineDecoration(paintColor: Int, dh: Int = 2) = LineItemDecoration(paintColor, dh)
}


class LineItemDecoration(paintColor: Int, private var dividerHeight: Int) : RecyclerView.ItemDecoration() {
    private var dividerPaint = Paint()

    init {
        dividerPaint.color = paintColor
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        outRect?.bottom = dividerHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        var childCount = parent?.childCount

        var left = parent?.paddingLeft?.toFloat()!!
        var right = parent.width - parent.paddingRight.toFloat()

        for (i in 2 until childCount!! - 2) {
            var view = parent.getChildAt(i)
            var top = view?.bottom?.toFloat()!!
            var bottom = view.bottom.toFloat() + dividerHeight
            c?.drawRect(left, top, right, bottom, dividerPaint)
        }
    }
}