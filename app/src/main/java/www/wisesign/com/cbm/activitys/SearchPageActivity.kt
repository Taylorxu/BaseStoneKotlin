package www.wisesign.com.cbm.activitys

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.xuzhiguang.xzglibrary.http.FlatMapResponse
import com.xuzhiguang.xzglibrary.http.MySubscriber
import com.xuzhiguang.xzglibrary.view.XAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.BR
import www.wisesign.com.cbm.databinding.ActivitySearchPageBinding
import www.wisesign.com.cbm.databinding.ItemSearchBankBinding
import www.wisesign.com.cbm.model.SystemWorkingCaseBean
import www.wisesign.com.cbm.model.WrapMemberTradeBean
import www.wisesign.com.cbm.request.ApiService

import java.util.ArrayList
import java.util.HashMap

class SearchPageActivity : AppCompatActivity(), TextWatcher {

    internal var topList: MutableList<SystemWorkingCaseBean> = ArrayList()
    internal var selectEdList: MutableList<SystemWorkingCaseBean> = ArrayList()
    lateinit var binding: ActivitySearchPageBinding
    internal var adapter: XAdapter<SystemWorkingCaseBean, ItemSearchBankBinding> =
        XAdapter.SimpleAdapter(BR.data, R.layout.item_search_bank) { data, _ -> onItemClick(data) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSysBank()
        getTopData()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_page)
        with(binding) {
            title.text = resources.getString(R.string.str_bank_select)
            textBankSearch.addTextChangedListener(this@SearchPageActivity)
            contentBankList.layoutManager = LinearLayoutManager(this@SearchPageActivity)
            contentBankList.adapter = adapter
        }


    }

    //获取top界面的数据进行默认的显示
    private fun getTopData() {
        ApiService.get().tradeBankTopServlet()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t -> FlatMapResponse.callResponse(t) }
            .flatMap { t -> FlatMapResponse.callResult(t) }
            .subscribe(object : MySubscriber<WrapMemberTradeBean>() {
                override fun onNext(bean: WrapMemberTradeBean?) {
                    bean?.let {
                        topList.add(0, SystemWorkingCaseBean(TradeBankName = "全部", TradeBankCode = "-1"))
                        bean.BankList.forEach {
                            val newitem = SystemWorkingCaseBean(
                                TradeBankName = it.TradeBankName,
                                TradeBankCode = it.TradeBankCode
                            )
                            topList.add(newitem)
                        }
                    }
                    val dataAdapter = ArrayList<SystemWorkingCaseBean>()
                    dataAdapter.addAll(topList)
                    adapter.setList(dataAdapter)
                }
            })
    }


    //搜索框检索结果所用的数据不进行默认数据显示
    fun getSysBank() {
        val map = HashMap<String, String>()
        map["key"] = "Bank"
        ApiService.get().dictDataServlet(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t -> FlatMapResponse.callResponse(t) }
            .flatMap { t -> FlatMapResponse.callResult(t) }
            .subscribe(object : MySubscriber<MutableList<SystemWorkingCaseBean>>() {
                override fun onNext(list: MutableList<SystemWorkingCaseBean>?) {
                    selectEdList.addAll(list!!)
                }
            })
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        adapter.removeAll()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        val searchContent = binding.textBankSearch.text.toString()
        if (TextUtils.isEmpty(searchContent)) {
            adapter.setList(topList)
        } else {
            for (caseBean in selectEdList) {
                if (caseBean.TradeBankName.indexOf(s.toString()) > -1) {
                    adapter.addItem(caseBean)
                }
            }
        }

    }

    fun onItemClick(data: SystemWorkingCaseBean) {
        val intent = Intent()
        intent.putExtra("TradeBankName", data.TradeBankName)
        intent.putExtra("TradeBankCode", data.TradeBankCode)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun onClick(view: View) {
        finish()
    }


}


