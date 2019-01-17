package www.wisesign.com.cbm.fragments


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import www.wisesign.com.cbm.BR
import com.xuzhiguang.xzglibrary.http.FlatMapResponse
import com.xuzhiguang.xzglibrary.http.MySubscriber
import com.xuzhiguang.xzglibrary.view.XAdapter
import com.xuzhiguang.xzglibrary.view.recycleViewExtension.ItemDecorationEx.LineDecoration
import com.xuzhiguang.xzglibrary.view.recycleViewExtension.footer.LoadMoreFooterView
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.databinding.FragmentWarningHistoryListBinding
import www.wisesign.com.cbm.databinding.ItemWarningListBinding
import www.wisesign.com.cbm.model.AlarmSystemBean
import www.wisesign.com.cbm.request.ApiService
import www.wisesign.com.cbm.utils.Dateutil

class WarningHistoryListFragment : Fragment() {
    private var currentPage = 1
    lateinit var footerView: LoadMoreFooterView
    lateinit var myBinding: FragmentWarningHistoryListBinding
    private var myAdapter: XAdapter<AlarmSystemBean, ItemWarningListBinding> =
        XAdapter.SimpleAdapter(BR.data, R.layout.item_warning_list)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_warning_history_list, container, false)
        initView()
        return myBinding.root
    }

    private fun initView() {
        with(myBinding.container) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(LineDecoration(resources.getColor(R.color.color_line), 20))
            iAdapter = myAdapter
            footerView = this.loadMoreFooterView as LoadMoreFooterView
            setOnLoadMoreListener { loadMoreData() }
            setOnRefreshListener { refreshData() }
            post { myBinding.container.setRefreshing(true) }
        }
    }

    private fun refreshData() {
        EventBus.getDefault().post(Dateutil.currentDate(Dateutil.YMDHMS))
        currentPage = 1
        setFooterViewStatus(LoadMoreFooterView.Status.GONE)
        getData(currentPage)
    }

    private fun loadMoreData() {
        if (footerView.canLoadMore()) {
            setFooterViewStatus(LoadMoreFooterView.Status.LOADING)
            getData(currentPage)
        }
    }

    //刷新 和加载更多时都需要 管理底部的状态
    private fun setFooterViewStatus(status: LoadMoreFooterView.Status) {
        footerView.setStatus(status)
    }

    private fun getData(page: Int) {
        ApiService.get().historyAlarmServlet(page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t -> FlatMapResponse.callResponse(t) }
            .flatMap { t -> FlatMapResponse.callResult((t)) }
            .subscribe(object : MySubscriber<MutableList<AlarmSystemBean>>() {
                override fun onError(e: Throwable) {
                    myBinding.container.setRefreshing(false)
                    setFooterViewStatus(LoadMoreFooterView.Status.GONE)
                }

                override fun onNext(list: MutableList<AlarmSystemBean>?) {
                    myBinding.container.setRefreshing(false)
                    if (page == 1) {
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
