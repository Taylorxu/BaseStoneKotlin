package  www.wisesign.com.cbm.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xuzhiguang.xzglibrary.http.FlatMapResponse
import com.xuzhiguang.xzglibrary.http.MySubscriber
import com.xuzhiguang.xzglibrary.view.XAdapter
import com.xuzhiguang.xzglibrary.view.XViewHolder
//import kotlinx.android.synthetic.main.fragment_second.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.databinding.FragmentSecondBinding
import www.wisesign.com.cbm.databinding.ItemMemberTradeTopBinding
import www.wisesign.com.cbm.model.MemberTradeBean
import www.wisesign.com.cbm.model.WrapMemberTradeBean
import www.wisesign.com.cbm.request.ApiService
import www.wisesign.com.cbm.utils.Dateutil

class SecondFragment : Fragment() {
    lateinit var binding: FragmentSecondBinding
    var fragmentIFlistener: SecondFragmentIFlistenter? = null
    private var colorspan: IntArray? = null
    private var bankList: MutableList<MemberTradeBean>? = null

    private var refreshListener: SwipeRefreshLayout.OnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        createData()
        binding.title.text = getString(R.string.str_top_date_title) + " " + Dateutil.currentDate(Dateutil.YMDHMS)
    }

    private var adapter: XAdapter<MemberTradeBean, ItemMemberTradeTopBinding> =
        object : XAdapter.SimpleAdapter<MemberTradeBean, ItemMemberTradeTopBinding>(
            0,
            R.layout.item_member_trade_top,
            { itemData, _ -> onItemClick(itemData) }
        ) {
            override fun onBindViewHolder(
                holder: XViewHolder<MemberTradeBean, ItemMemberTradeTopBinding>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                if (position == 0 || position == 1 || position == 2) {
                    holder.binding.textNo.setTextColor(Color.RED)
                    holder.binding.textTradeNo.setTextColor(Color.RED)
                    holder.binding.layoutItem.setBackgroundColor(colorspan!![position])
                }
                var No = (position + 1).toString()
                if (position < 9) No = "0$No"
                holder.binding.textNo.text = No

                holder.binding.textMemberName.text = getItemData(position).TradeBankName
                holder.binding.textTradeNo.text = getItemData(position).TradeVolume
            }
        }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SecondFragmentIFlistenter) {
            fragmentIFlistener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
        colorspan = intArrayOf(
            resources.getColor(R.color.color_top1_b),
            resources.getColor(R.color.color_top2_b),
            resources.getColor(R.color.color_top3_b)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_second, container, false)
        with(binding) {
            refreshLayout.isRefreshing = true
            binding.title.text = getString(R.string.str_top_date_title) + " " + Dateutil.currentDate(Dateutil.YMDHMS)
            binding.refreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
            binding.refreshLayout.setOnRefreshListener(refreshListener)
            binding.contentAnnounceList.layoutManager = LinearLayoutManager(context)
            binding.contentAnnounceList.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
            binding.contentAnnounceList.adapter = adapter
        }
        createData()
        return binding.root
    }


    fun onItemClick(itemData: MemberTradeBean) {
        fragmentIFlistener?.refreshByBankCode(itemData)
    }


    private fun createData() {

        ApiService.get().tradeBankTopServlet()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t -> FlatMapResponse.callResponse(t) }
            .flatMap { t -> FlatMapResponse.callResult(t) }
            .subscribe(object : MySubscriber<WrapMemberTradeBean>() {

                override fun onError(e: Throwable) {
                    binding.refreshLayout.isRefreshing = false
                    if (null == bankList) setEmptyView(true)
                }

                override fun onNext(bean: WrapMemberTradeBean?) {
                    setEmptyView(bean == null)
                    bean?.BankList?.also { bankList = it }.let { adapter.setList(it!!) }
                    binding.refreshLayout.isRefreshing = false

                }
            })
    }


    private fun setEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.contentAnnounceList.visibility = View.GONE
            binding.tvEmptyView.visibility = View.VISIBLE
            binding.tvEmptyView.text = resources.getString(R.string.str_empty_data1)
        } else {
            binding.contentAnnounceList.visibility = View.VISIBLE
            binding.tvEmptyView.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fragmentIFlistener != null) fragmentIFlistener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentIFlistener = null
    }

    interface SecondFragmentIFlistenter {
        fun refreshByBankCode(data: MemberTradeBean)
    }

    companion object {

        fun newInstance(): SecondFragment {
            return SecondFragment()
        }
    }
}