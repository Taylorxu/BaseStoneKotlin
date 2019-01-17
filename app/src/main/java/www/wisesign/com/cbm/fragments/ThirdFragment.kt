package  www.wisesign.com.cbm.fragments

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.databinding.FragmentThirdBinding
import www.wisesign.com.cbm.utils.Dateutil

class ThirdFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentThirdBinding
    lateinit var pageTitle: Array<String>

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        pageTitle = arrayOf(
            resources.getString(R.string.str_warning_list),
            resources.getString(R.string.str_warning_history)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_third, container, false)
        initView()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
        return binding.root
    }

    private fun initView() {
        binding.title.text = getString(R.string.str_warning_title) + " " + Dateutil.currentDate(Dateutil.YMDHMS)
        binding.contentVp.adapter = WarningPageAdapter(childFragmentManager, pageTitle)
        binding.warningTabL.setupWithViewPager(binding.contentVp)
    }

    override fun onClick(view: View) {

    }

    @Subscribe
    fun setTitle(title: String) {
        binding.title.text = getString(R.string.str_warning_title) + " " + title
    }


    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    companion object {

        fun newInstance(): ThirdFragment {
            return ThirdFragment()
        }
    }


    class WarningPageAdapter(fm: FragmentManager, internal var title: Array<String>) : FragmentPagerAdapter(fm) {

        private var fragments = arrayOf(WarningListFragment(), WarningHistoryListFragment())

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return title[position]
        }
    }
}