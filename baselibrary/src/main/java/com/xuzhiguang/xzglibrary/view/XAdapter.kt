package com.xuzhiguang.xzglibrary.view

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by 徐志广 on 2018/4/11.
 */
open abstract class XAdapter<Data, Binding : ViewDataBinding> : RecyclerView.Adapter<XViewHolder<Data, Binding>>() {
    var dataList = mutableListOf<Data>()

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): XViewHolder<Data, Binding> {
        var viewHolder: XViewHolder<Data, Binding>? =
            XViewHolder(LayoutInflater.from(p0?.context).inflate(holderLayout(p1), p0, false))
        return viewHolder!!
    }

    abstract fun holderLayout(viewType: Int): Int

    /**
     *variableId 是R.layout.xxx 中<layout><data></data></layout> BR.data
     *在UI中重写 并自己处理数据时 约定俗成variableId=0
     */

    open class SimpleAdapter<Data, Binding : ViewDataBinding>(
        private var variableId: Int,
        private var holderLayout: Int,
        private var itemClick: ((Data, Binding) -> Unit)? = null
    ) : XAdapter<Data, Binding>() {

        override fun onBindViewHolder(p0: XViewHolder<Data, Binding>, p1: Int) {
            if (variableId != 0) p0?.fill(variableId, dataList!![p1])
            p0?.itemView?.setOnClickListener { itemClick?.let { it1 -> it1(dataList!![p1], p0?.binding) } }
        }


        //holderLayout 是R.layout.xxx 布局文件在资源中ID
        override fun holderLayout(viewType: Int): Int {
            return holderLayout
        }


    }

    //初始
    fun setList(l: MutableList<Data>) {
        dataList = l
        notifyDataSetChanged()
    }

    fun getItemData(position: Int) = dataList[position]

    //追加list
    fun addItems(l: MutableList<Data>) {
        if (l === null || l.isEmpty()) return
        dataList.addAll(l)
        notifyItemInserted(dataList.size.let { it - 1 })
        notifyItemRangeChanged(dataList.size - l.size, l.size)
    }

    //追加item
    fun addItem(data: Data) {
        dataList.add(data)
        notifyItemInserted(dataList.size.let { it - 1 })
    }

    //指定位置
    fun addItem(data: Data, position: Int) {
        dataList.add(position, data)
        notifyItemInserted(position)
    }

    //删除指定
    fun removeItem(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    //删除全部
    fun removeAll() {
        dataList.clear()
        notifyDataSetChanged()
    }
}