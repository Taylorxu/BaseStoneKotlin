package www.wisesign.com.cbm.fragments


import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xuzhiguang.xzglibrary.http.FlatMapResponse
import com.xuzhiguang.xzglibrary.http.MySubscriber
import com.xuzhiguang.xzglibrary.view.XAdapter
import com.xuzhiguang.xzglibrary.view.recycleViewExtension.ItemDecorationEx.LineDecoration
import com.xuzhiguang.xzglibrary.view.recycleViewExtension.footer.LoadMoreFooterView
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import www.wisesign.com.cbm.BR
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.databinding.FragmentWarningListBinding
import www.wisesign.com.cbm.databinding.ItemWarningListBinding
import www.wisesign.com.cbm.model.AlarmSystemBean
import www.wisesign.com.cbm.request.ApiService
import www.wisesign.com.cbm.utils.Dateutil


class WarningListFragment : Fragment() {
    private var currentPage: Int = 1
    lateinit var binding: FragmentWarningListBinding
    private var myAdapter: XAdapter<AlarmSystemBean, ItemWarningListBinding> =
        XAdapter.SimpleAdapter(BR.data, R.layout.item_warning_list)

    lateinit var footerView: LoadMoreFooterView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_warning_list, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        with(binding.container) {
            addItemDecoration(LineDecoration(resources.getColor(R.color.color_line), 20))
            layoutManager = LinearLayoutManager(context)
            iAdapter = myAdapter
            setOnRefreshListener { refreshData() }
            setOnLoadMoreListener { loadMoreData() }
            footerView = this.loadMoreFooterView as LoadMoreFooterView
            post { binding.container.setRefreshing(true) }
        }
    }


    private fun refreshData() {
        EventBus.getDefault().post(Dateutil.currentDate(Dateutil.YMDHMS))
        currentPage = 1
        setFooterViewStatus(LoadMoreFooterView.Status.GONE)
        getData(currentPage.toString())
    }

    private fun loadMoreData() {
        if (footerView.canLoadMore()) {
            setFooterViewStatus(LoadMoreFooterView.Status.LOADING)
            getData(currentPage.toString())
        }
    }

    //刷新 和加载更多时都需要 管理底部的状态
    private fun setFooterViewStatus(status: LoadMoreFooterView.Status) {
        footerView.setStatus(status)
    }

    private fun getData(page: String) {
        ApiService.get().currentAlarmServlet(page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t -> FlatMapResponse.callResponse(t) }
            .flatMap { t -> FlatMapResponse.callResult((t)) }
            .subscribe(object : MySubscriber<MutableList<AlarmSystemBean>>() {
                override fun onError(e: Throwable) {
                    setFooterViewStatus(LoadMoreFooterView.Status.GONE)
                    binding.container.setRefreshing(false)
                }

                override fun onNext(list: MutableList<AlarmSystemBean>?) {
                    binding.container.setRefreshing(false)
                    if (page == "1") {
                        list?.let { myAdapter.setList(it) }
                    } else {
                        list?.let { myAdapter.addItems(it) }
                    }
                    setFooterViewStatus(if (list?.size!! < 20) LoadMoreFooterView.Status.THE_END else LoadMoreFooterView.Status.GONE)
                    currentPage += 1
                }
            })

    }

}
