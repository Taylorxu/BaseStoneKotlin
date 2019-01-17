package www.wisesign.com.cbm.activitys

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.xuzhiguang.xzglibrary.http.FlatMapResponse
import com.xuzhiguang.xzglibrary.http.MySubscriber
import com.xuzhiguang.xzglibrary.view.XAdapter
import com.xuzhiguang.xzglibrary.view.XViewHolder
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.databinding.ActivityBankDetailDataBinding
import www.wisesign.com.cbm.databinding.ItemOneBankBinding
import www.wisesign.com.cbm.model.OneSystemBean
import www.wisesign.com.cbm.model.WrapOnSystemBean
import www.wisesign.com.cbm.request.ApiService

import java.io.Serializable
import java.util.ArrayList
import java.util.Collections

class BankDetailDataActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var binding: ActivityBankDetailDataBinding
    private var tradebankName = ""
    private var tradeBankCode = ""
    private var tradeSysCode = ""

    private var pieCharts: Array<PieChart>? = null
    internal var adapter: XAdapter<OneSystemBean, ItemOneBankBinding> =
        object : XAdapter.SimpleAdapter<OneSystemBean, ItemOneBankBinding>(0, R.layout.item_one_bank) {
            override fun onBindViewHolder(holder: XViewHolder<OneSystemBean, ItemOneBankBinding>, position: Int) {
                val systemBean = getItemData(position)
                with(holder.binding) {
                    textDateHeader.text = systemBean.DateTime
                    textTotal.text = formatNumber(systemBean.TradeVolume)
                    tvDynamic.text = formatNumber(systemBean.TradeDynamicVolume)
                    tvStatic.text = formatNumber(systemBean.TradeStaticVolume)
                }
                pieCharts = arrayOf(
                    holder.binding.pieChartEmergency,
                    holder.binding.pieChartUrgency,
                    holder.binding.pieChartNormal
                )
                setPieChartData(systemBean)
            }
        }

    @SuppressLint("Range")
    private val mtextColors = intArrayOf(Color.rgb(255, 90, 107), Color.rgb(255, 199, 66), Color.rgb(38, 222, 138))

    @SuppressLint("Range")
    private val mColors = arrayOf(
        intArrayOf(Color.rgb(255, 90, 107), Color.rgb(229, 229, 229)),
        intArrayOf(Color.rgb(255, 199, 66), Color.rgb(229, 229, 229)),
        intArrayOf(Color.rgb(38, 222, 138), Color.rgb(229, 229, 229))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bank_detail_data)
        getExtra()
        with(binding) {
            refreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
            refreshLayout.setOnRefreshListener(this@BankDetailDataActivity)
            contentData.layoutManager = LinearLayoutManager(this@BankDetailDataActivity)
            contentData.adapter = adapter
        }

    }


    private fun getExtra() {
        val stringMap = intent.getSerializableExtra(BANKNAMEKEY) as Map<String, String>
        stringMap?.let {
            tradebankName = it["BankName"].toString()
            tradeSysCode = it["TradeSysCode"].toString()
            tradeBankCode = it["TradeBankCode"].toString()
            binding.title.text = it["SysName"]
        }
        binding.textBankSearch.text = tradebankName
        getData(tradeBankCode, tradeSysCode)
    }

    private fun getData(tradeBankCode: String?, tradeSysCode: String?) {
        ApiService.get().oneSystemStatusServlet(tradeSysCode!!, tradeBankCode!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t -> FlatMapResponse.callResponse(t) }
            .subscribe(object : MySubscriber<WrapOnSystemBean>() {

                override fun onError(e: Throwable) {
                    binding.refreshLayout.isRefreshing = false
                }

                override fun onNext(bean: WrapOnSystemBean?) {
                    if (null != bean?.current || null != bean?.history) {
                        showEmpty(false)
                        val adapterList = ArrayList<OneSystemBean>()
                        adapterList.add(bean.current)
                        adapterList.add(bean.history)
                        adapter.setList(adapterList)
                    } else {
                        Toast.makeText(baseContext, bean?.Reason, Toast.LENGTH_SHORT).show()
                        showEmpty(true)
                    }
                    binding.refreshLayout.isRefreshing = false
                }
            })
    }

    private fun showEmpty(isVisible: Boolean) {
        binding.tvEmpty.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvEmpty.text = if (isVisible) resources.getString(R.string.str_empty_data) else ""
        binding.contentData.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private fun setPieChartData(data: OneSystemBean) {
        initPieCHartView()
        for (i in pieCharts!!.indices) {
            val entries = ArrayList<PieEntry>()
            when (i) {
                0 -> {
                    entries.add(PieEntry(java.lang.Float.parseFloat(data.TradeSucRate)))
                    entries.add(PieEntry(100 - java.lang.Float.parseFloat(data.TradeSucRate)))
                    pieCharts!![i].centerText = generateCenterSpannableText(i, data.TradeSucRate)
                }
                1 -> {
                    entries.add(PieEntry(java.lang.Float.parseFloat(data.TradeStaticSucRate)))
                    entries.add(PieEntry(100 - java.lang.Float.parseFloat(data.TradeStaticSucRate)))
                    pieCharts!![i].centerText = generateCenterSpannableText(i, data.TradeStaticSucRate)
                }
                else -> {
                    entries.add(PieEntry(java.lang.Float.parseFloat(data.TradeDynamicSucRate)))
                    entries.add(PieEntry(100 - java.lang.Float.parseFloat(data.TradeDynamicSucRate)))
                    pieCharts!![i].centerText = generateCenterSpannableText(i, data.TradeDynamicSucRate)
                }
            }

            val dataSet = PieDataSet(entries, "PieData$i").apply {
                setDrawIcons(false)
                sliceSpace = 1f
                selectionShift = 5f
                setColors(*mColors[i])
            }


            val pieData = PieData(dataSet)
            pieData.setDrawValues(false)//不在环形上显示数据

            pieCharts!![i].data = pieData
            pieCharts!![i].invalidate()
        }
    }

    private fun initPieCHartView() {
        for (i in pieCharts!!.indices) {
            pieCharts!![i].setUsePercentValues(true)
            pieCharts!![i].description.isEnabled = false
            pieCharts!![i].legend.isEnabled = false

            pieCharts!![i].dragDecelerationFrictionCoef = 0.95f

            pieCharts!![i].isDrawHoleEnabled = true//置空中间,否则是扇形图
            pieCharts!![i].setHoleColor(Color.WHITE)
            pieCharts!![i].setDrawCenterText(true)
            pieCharts!![i].setCenterTextSize(12f)


            pieCharts!![i].setTransparentCircleColor(Color.WHITE)
            pieCharts!![i].setTransparentCircleAlpha(110)

            pieCharts!![i].holeRadius = 58f
            pieCharts!![i].transparentCircleRadius = 61f

            // 旋转
            pieCharts!![i].rotationAngle = 0f
            pieCharts!![i].isRotationEnabled = true
            pieCharts!![i].isHighlightPerTapEnabled = true
            pieCharts!![i].animateY(1400, Easing.EasingOption.EaseInOutQuad)

        }
    }

    @SuppressLint("ResourceAsColor")
    private fun generateCenterSpannableText(p: Int, pieChartData: String): SpannableString {
        var pieChartData = pieChartData
        var s: SpannableString
        if (pieChartData == "100.00") {
            pieChartData = "100"
        }
        s = SpannableString("$pieChartData%")
        s.setSpan(StyleSpan(Typeface.NORMAL), 0, s.length, 0)
        s.setSpan(ForegroundColorSpan(mtextColors[p]), 0, s.length, 0)

        return s
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back -> finish()
            R.id.btn_search_frame -> {
                val intent = Intent(this, SearchPageActivity::class.java)
                startActivityForResult(intent, 9008)
            }
        }
    }

    override fun onRefresh() {
        getData(tradeBankCode, tradeSysCode)
    }

    /**
     * 从筛选界面返回来
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 9008) {
                tradebankName = data!!.getStringExtra("TradeBankName")
                tradeBankCode = data.getStringExtra("TradeBankCode")
                binding.textBankSearch.text = tradebankName
                onRefresh()
            }
        }
    }


    fun formatNumber(number: String): String {
        var where = 0
        val finalNumber = StringBuffer()
        val formatNumber = ArrayList<String>()
        if (number.length > 3) {
            val charArray = number.toCharArray()
            for (i in charArray.indices.reversed()) {
                if (where != 0 && where % 3 == 0) {
                    formatNumber.add(",")
                }
                formatNumber.add(charArray[i].toString())
                where++
            }
        } else {
            return number
        }
        formatNumber.reverse()
        for (s in formatNumber) {
            finalNumber.append(s)
        }
        return finalNumber.toString()
    }

    companion object {
        var BANKNAMEKEY = "BANKNAME"

        fun start(context: Context, stringMap: Map<String, String>) {
            val starter = Intent(context, BankDetailDataActivity::class.java)
            starter.putExtra(BANKNAMEKEY, stringMap as Serializable)
            context.startActivity(starter)
        }
    }
}
