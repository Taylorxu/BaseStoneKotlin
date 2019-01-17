package www.wisesign.com.cbm.activitys

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.xuzhiguang.xzglibrary.BaseApplication
import com.xuzhiguang.xzglibrary.helperTool.*
import com.xuzhiguang.xzglibrary.rx.Notification
import com.xuzhiguang.xzglibrary.rx.RxBus
import com.xuzhiguang.xzglibrary.view.xViewElement.XNavigationBar
import kotlinx.android.synthetic.main.activity_main.*
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.fragments.*
import www.wisesign.com.cbm.model.MemberTradeBean
import java.util.*

class MainActivity : AppCompatActivity(), SecondFragment.SecondFragmentIFlistenter {

    private val firstFragment: Fragment by lazy { FirstFragment.newInstance() }
    private val secondFragment: Fragment by lazy { SecondFragment.newInstance() }
    private val thirdFragment: Fragment by lazy { ThirdFragment.newInstance() }
    private val fourthFragment: Fragment by lazy { FourthFragment.newInstance() }
    private val fifthFragment: Fragment by lazy { FifthFragment.newInstance() }
    private val fragmentList = listOf(firstFragment, secondFragment, thirdFragment, fifthFragment, fourthFragment)
    var showPreviousNav: String by SharePreference.perference(
        BaseApplication.instance.baseContext,
        PreferenceKeList.oldNavVisibility,
        "0"
    )

    var showNewNav: String by SharePreference.perference(
        BaseApplication.instance.baseContext,
        PreferenceKeList.newNavVisibility,
        "0"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation_bar_group.setOnCheckedChangeListenter(barCheckListener)
        //TODO 根据shareperference 中的权限 去显示 导航
        initNavigationView()
        addFragment(null, fragmentList, R.id.frame_layout_content)
        showOrhideFragment(fragmentList[0])

    }

    /**
     *初始化底部导航栏 有可能 显示原有和新的共5个 或者原有的 4个 或者显示两个
     */
    private fun initNavigationView() {
        val oldNav =
            mutableListOf(R.drawable.selector_ic_tab1, R.drawable.selector_ic_tab2, R.drawable.selector_ic_tab3)
        val newNav = mutableListOf(R.drawable.selector_ic_tab5)

        var drawableResArray = mutableListOf<Int>()
        if (showPreviousNav == "1") {
            drawableResArray.addAll(oldNav)
        } else { //如果不存在旧的导航按钮 占位 处理
            for (i in 0..2) {
                drawableResArray.add(i)
            }
        }
        if (showNewNav == "1") {
            drawableResArray.addAll(newNav)
        }else{
            drawableResArray.add(3)
        }
        drawableResArray.add(R.drawable.selector_ic_tab4)  //最后的设置
        navigation_bar_group.setDrawableTop(drawableResArray)
    }


    /**
     *显示或隐藏fragment
     */
    fun showOrhideFragment(fragment: Fragment) {
        showFragmentToActivity(fragment)
        fragmentList.filterNot { fragment === it }.forEach {
            hideFragmentToActivity(it)
        }
    }

    /**
     * 添加fragment 初始换
     */
    private fun addFragment(fragment: Fragment?, listFragment: List<Fragment>?, containerViewId: Int) {
        listFragment?.forEach {
            addFragmentToActivity(containerViewId, it)
        }
    }


    private var barCheckListener = object : XNavigationBar.OnCheckedChangeListener {
        override fun onCheckedChanged(checkedId: Int) {
            when (checkedId) {
                com.xuzhiguang.xzglibrary.R.id.rb_0 -> {
                    showOrhideFragment(firstFragment)
                }
                com.xuzhiguang.xzglibrary.R.id.rb_1 -> {
                    showOrhideFragment(secondFragment)
                }
                com.xuzhiguang.xzglibrary.R.id.rb_2 -> {
                    showOrhideFragment(thirdFragment)
                }
                com.xuzhiguang.xzglibrary.R.id.rb_3 -> {
                    showOrhideFragment(fifthFragment)
                }
                com.xuzhiguang.xzglibrary.R.id.rb_4 -> {
                    showOrhideFragment(fourthFragment)
                    //TODO 业务支付fragment show
                }

            }
        }

    }

    override fun refreshByBankCode(data: MemberTradeBean) {
        navigation_bar_group.rbView[0].isChecked = true
        RxBus.default?.post(Notification(1, 0).setExtra(data))
    }

    override fun onBackPressed() {
        var mBackKeyPressed = false
        if (!mBackKeyPressed) {
            NiceToast.toast("再按一次退出程序")
            mBackKeyPressed = true
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    mBackKeyPressed = false
                }
            }, 4000)

        } else {
            finish()
            System.exit(0)
        }
    }


}
