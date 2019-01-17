package  www.wisesign.com.cbm.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.xuzhiguang.xzglibrary.http.FlatMapResponse
import com.xuzhiguang.xzglibrary.http.MySubscriber
import com.xuzhiguang.xzglibrary.rx.Notification
import com.xuzhiguang.xzglibrary.view.XAdapter
import com.xuzhiguang.xzglibrary.view.XViewHolder
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.activitys.BankDetailDataActivity
import www.wisesign.com.cbm.activitys.SearchPageActivity
import www.wisesign.com.cbm.databinding.FragmentFirstBinding
import www.wisesign.com.cbm.databinding.ItemFirstFragmentBinding
import www.wisesign.com.cbm.model.MemberTradeBean
import www.wisesign.com.cbm.model.SystemWorkingCaseBean
import www.wisesign.com.cbm.request.ApiService
import www.wisesign.com.cbm.utils.Dateutil
import java.util.ArrayList
import java.util.HashMap

class FirstFragment : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    lateinit var fragmentBinding: FragmentFirstBinding
    var caseBeanList: MutableList<SystemWorkingCaseBean>? = null
    /**
     * 默认-1 全部。 当在搜索界面选好银行后，跳转此界面 根据此条件查询
     */
    var tradeBankCode = "-1"
    var tradebankName = "全部"
    /**
     * 从top排行界面 itemclick ------>mainActivity{refreshByBankCode}-------->FirstFragment
     */
    private var action1: Action1<Notification> =
        Action1 { notification ->
            if (notification.code === 1) {
                val tradeBean = notification.getExtra() as MemberTradeBean
                tradebankName = tradeBean.TradeBankName
                tradeBankCode = tradeBean.TradeBankCode
                setradebankName()
                fragmentBinding.refreshLayout.isRefreshing = true
                createData()

            }
        }
    private var xAdapter: XAdapter<SystemWorkingCaseBean, ItemFirstFragmentBinding> = object :
        XAdapter.SimpleAdapter<SystemWorkingCaseBean, ItemFirstFragmentBinding>(
            0,
            R.layout.item_first_fragment,
            { data, _ -> onAdapterItemClick(data) }) {
        override fun onBindViewHolder(
            holder: XViewHolder<SystemWorkingCaseBean, ItemFirstFragmentBinding>,
            position: Int
        ) {
            super.onBindViewHolder(holder, position)
            val bean = getItemData(position)
            holder.binding.data = bean
            initPieCHartView(bean, holder.binding.pieChartView)

        }
    }

    override fun onResume() {
        super.onResume()
        getBaseData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Notification.register(action1)
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false)
        with(fragmentBinding) {
            title.text = getString(R.string.str_system_case) + " " + Dateutil.currentDate(Dateutil.YMDHMS)
            refreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
            refreshLayout.setOnRefreshListener(this@FirstFragment)
            btnSearchFrame.setOnClickListener(this@FirstFragment)
        }
        setradebankName()
        initListView()
        return fragmentBinding.root
    }

    private fun initPieCHartView(bean: SystemWorkingCaseBean, pieCharts: PieChart) {
        with(pieCharts) {
            setTouchEnabled(false)
            setUsePercentValues(true)
            description.isEnabled = false
            legend.isEnabled = false
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true//置空中间,否则是扇形图
            setHoleColor(Color.WHITE)
            setDrawCenterText(true)
            setCenterTextSize(12f)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            rotationAngle = 0f
            isRotationEnabled = false
            isHighlightPerTapEnabled = false
            animateY(1400, Easing.EasingOption.EaseInOutQuad)
        }
        setPieChartData(bean, pieCharts)
    }

    /**
     * 初始化 recycleVIew 、
     * 查询数据
     */
    private fun initListView() {
        if (null != caseBeanList) createData()
        val layoutmanager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        fragmentBinding.let {
            it.contentCase.layoutManager = layoutmanager
            it.contentCase.adapter = xAdapter
            it.contentCase.addItemDecoration(RVItemDecoration(4, 20))
        }


    }

    //给环形组件赋数据
    private fun setPieChartData(data: SystemWorkingCaseBean, pieChart: PieChart) {
        val prograss = data.TradeSysSucRate.toFloat()
        val centerTitle = data.TradeSysSucRate
        val pieChartColor = Color.parseColor(data.TradeSysColour.toUpperCase())
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(prograss))
        entries.add(PieEntry(100 - prograss))
        pieChart.centerText = generateCenterSpannableText(centerTitle)

        PieDataSet(entries, "PieData").also {
            it.setDrawIcons(false)
            it.sliceSpace = 0f
            it.selectionShift = 0f
            it.setColors(pieChartColor, Color.rgb(229, 229, 229))
            val pieData = PieData(it)
            pieData.setDrawValues(false)
            pieChart.data = pieData
            pieChart.invalidate()
        }


    }


    /**
     * 全部 银行 tradeBankCode=-1 ，
     * 根据 tradeBankCode 参数查询出交易量的数据，在和系统数据集合进行合并
     */
    private fun createData() {
        ApiService.get().systemStatusServlet(tradeBankCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t -> FlatMapResponse.callResponse(t) }
            .flatMap { t -> FlatMapResponse.callResult(t) }
            .subscribe(object : MySubscriber<MutableList<SystemWorkingCaseBean>>() {
                override fun onError(e: Throwable) {
                    fragmentBinding.refreshLayout.isRefreshing = false
                }

                override fun onNext(list: MutableList<SystemWorkingCaseBean>?) {
                    list?.let {
                        for (beanSys in caseBeanList!!) {
                            for (bank in list) {
                                if (bank.TradeSysCode == beanSys.TradeSysCode) {
                                    beanSys.TradeSysVolume = bank.TradeSysVolume
                                    beanSys.TradeSysSucRate = bank.TradeSysSucRate
                                }
                            }
                        }
                        xAdapter.setList(caseBeanList!!)
                    }
                    fragmentBinding.refreshLayout.isRefreshing = false
                }
            })
    }

    /**
     * 获取系统数据
     */
    private fun getBaseData() {
        val map = HashMap<String, String>()
        map["key"] = "BusinessSystem"
        map["tradeBankCode"] = tradeBankCode
        ApiService.get().dictDataServlet(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t -> FlatMapResponse.callResponse(t) }
            .flatMap { t -> FlatMapResponse.callResult(t) }
            .subscribe(object : MySubscriber<MutableList<SystemWorkingCaseBean>>() {
                override fun onCompleted() {
                    if (null != caseBeanList) createData()
                }

                override fun onError(e: Throwable) {
                    if (null == caseBeanList) setEmptyView(true)
                    fragmentBinding.refreshLayout.isRefreshing = false
                }

                override fun onNext(list: MutableList<SystemWorkingCaseBean>?) {
                    setEmptyView(list?.size == 0)
                    caseBeanList = list
                    fragmentBinding.refreshLayout.isRefreshing = false
                }
            })
    }

    /**
     * 跳转到银行搜索界面
     *
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_search_frame -> {
                val intent = Intent(activity, SearchPageActivity::class.java)
                startActivityForResult(intent, 9009)
            }
        }
    }

    /**
     * item 跳转到BankDetailDataActivity 每个系统下的某个银行（或全部）详细环比
     * TradeBankCode、TradeSysCode 查询参数
     * SysName title
     * BankName 界面银行名称
     */
    fun onAdapterItemClick(data: SystemWorkingCaseBean) {
        val stringMap = HashMap<String, String>()
        stringMap["TradeBankCode"] = tradeBankCode
        stringMap["TradeSysCode"] = data.TradeSysCode
        stringMap["SysName"] = data.TradeSysName
        stringMap["BankName"] = tradebankName
        BankDetailDataActivity.start(context!!, stringMap)
    }

    /**
     * 银行搜索跳转TextView 展示
     */
    private fun setradebankName() {
        fragmentBinding.textBankSearch.text = tradebankName
    }

    /**
     * 从筛选界面返回来
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 9009) {
                tradebankName = data!!.getStringExtra("TradeBankName")
                tradeBankCode = data.getStringExtra("TradeBankCode")
                setradebankName()
                getBaseData()
            }
        }
    }


    override fun onRefresh() {
        getBaseData()
        fragmentBinding.title.text = getString(R.string.str_system_case) + " " + Dateutil.currentDate(Dateutil.YMDHMS)
    }

    private fun setEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            fragmentBinding.contentCase.visibility = View.GONE
            fragmentBinding.tvEmptyView.visibility = View.VISIBLE
            fragmentBinding.tvEmptyView.text = resources.getString(R.string.str_empty_data1)
        } else {
            fragmentBinding.tvEmptyView.visibility = View.GONE
            fragmentBinding.contentCase.visibility = View.VISIBLE
        }
    }

    /**
     * 为recycleView 的每一个item 描绘 分割线
     */
    internal inner class RVItemDecoration(private val space: Int, private val mDividerHeight: Int) :
        RecyclerView.ItemDecoration() {
        private val paint: Paint
        private val paint1: Paint

        init {
            this.paint = Paint()
            this.paint1 = Paint()
            paint.isAntiAlias = true
            paint.color = resources.getColor(R.color.color_6f5)
            paint1.isAntiAlias = true
            paint1.color = resources.getColor(R.color.color_e6)
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if (parent.getChildLayoutPosition(view) % 2 == 0) {
                outRect.right = space
            }
            outRect.top = mDividerHeight
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
            val childCount = parent.childCount

            for (i in 0 until childCount) {////描画  每条分割线
                val view = parent.getChildAt(i)
                // view.getTop() 所描述的是距离，不是点
                val dividerTop = (view.top - mDividerHeight).toFloat()
                val dividerLeft = parent.paddingLeft.toFloat()
                val dividerBottom = view.top.toFloat()
                val dividerRight = (parent.width - parent.paddingRight).toFloat()
                c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, paint)

            }
            for (i in 0 until childCount) {//描画 每个 item右边的竖条分割线
                val view = parent.getChildAt(i)
                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    val dividerRight = (view.right + space).toFloat()
                    val dividerLeft = (view.right - view.left).toFloat()
                    c.drawRect(dividerLeft, view.top.toFloat(), dividerRight, view.bottom.toFloat(), paint1)
                }

            }
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun generateCenterSpannableText(pieChartData: String): SpannableString {
        var pieChartData = pieChartData
        pieChartData = if (pieChartData == "100.00") "100%" else "$pieChartData%"
        //        String centerText = Float.valueOf(pieChartData) + "%";
        val s: SpannableString
        s = SpannableString(pieChartData)
        s.setSpan(StyleSpan(Typeface.NORMAL), 0, s.length, 0)
        s.setSpan(ForegroundColorSpan(Color.BLACK), 0, s.length, 0)

        return s
    }

    companion object {

        fun newInstance(): FirstFragment {
            return FirstFragment()
        }
    }

}