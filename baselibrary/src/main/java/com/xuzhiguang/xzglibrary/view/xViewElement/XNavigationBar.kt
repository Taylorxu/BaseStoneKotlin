package com.xuzhiguang.xzglibrary.view.xViewElement

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import com.xuzhiguang.xzglibrary.R
import com.xuzhiguang.xzglibrary.helperTool.DensityHelper
import kotlinx.android.synthetic.main.layout_navigation_bar_group.view.*
import org.jetbrains.anko.forEachChild

/**
 * 自定义 navigationBar
 * Created by 徐志广 on 2018/4/24.
 */
class XNavigationBar : LinearLayout {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var onCheckedChangeInterface: OnCheckedChangeListener? = null
    var p_top_c = DensityHelper.dp2px(8f)  //被选中时，paddingTop
    var p_top_ = DensityHelper.dp2px(10f)
    var p_bottom = DensityHelper.dp2px(8f)
    val text_size_c = 12f  //被选中时，字体大小设置为 12
    val text_size_ = 11f

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_navigation_bar_group, this, true)
        rb_0.isChecked = true
        initViewParam()
        rb_group.setOnCheckedChangeListener { group, checkedId -> onCheckedChangeListener(checkedId) }
    }

    private fun initViewParam() {
        rb_group.forEachChild { view -> setEachChild(view as RadioButton) }
    }

    val rbView = arrayOf(rb_0, rb_1, rb_2, rb_3, rb_4)
    //在Mainactivity中给底部导航设置 drawableTop
    fun setDrawableTop(drawableResource: MutableList<Int>) {
        for ((index, value) in drawableResource.withIndex())
            if (value != index) {//不需要显示的radioButton value 与 游标相等
                rbView[index].visibility = View.VISIBLE
                rbView[index].setCompoundDrawablesWithIntrinsicBounds(0, value, 0, 0)
            }

    }


    fun setEachChild(v: RadioButton) {
        if (v.isChecked) {
            v.textSize = text_size_c
            v.setPadding(0, p_top_c, 0, p_bottom)
        } else {
            v.setPadding(0, p_top_, 0, p_bottom)
            v.textSize = text_size_
        }

    }

    var oldChecked: RadioButton? = null
    fun onCheckedChangeListener(checkedId: Int) {
        when (checkedId) {
            R.id.rb_0 -> {
                reSetOldChecked(rb_0)
            }
            R.id.rb_1 -> {
                reSetOldChecked(rb_1)
            }
            R.id.rb_2 -> {
                reSetOldChecked(rb_2)
            }
            R.id.rb_3 -> {
                reSetOldChecked(rb_3)
            }

        }
    }

    fun reSetOldChecked(newChecked: RadioButton) {
        newChecked.setPadding(0, p_top_c, 0, p_bottom)
        newChecked.textSize = text_size_c
        oldChecked?.setPadding(0, p_top_, 0, p_bottom)
        oldChecked?.textSize = text_size_
        oldChecked = newChecked
        onCheckedChangeInterface?.onCheckedChanged(newChecked.id)
    }

    fun setOnCheckedChangeListenter(listener: OnCheckedChangeListener) {
        if (onCheckedChangeInterface === null) onCheckedChangeInterface = listener
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(checkedId: Int)
    }

}