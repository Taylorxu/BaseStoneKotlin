package  www.wisesign.com.cbm.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xuzhiguang.xzglibrary.BaseApplication
import com.xuzhiguang.xzglibrary.helperTool.PreferenceKeList
import com.xuzhiguang.xzglibrary.helperTool.SharePreference
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.databinding.FragmentFourthBinding
import www.wisesign.com.cbm.databinding.LogOutDialogViewBinding
import www.wisesign.com.cbm.login.LoginActivity


class FifthFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentFourthBinding
    var islogin by SharePreference.perference(
        BaseApplication.instance.baseContext,
        PreferenceKeList.isLogIn,
        false
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fourth, container, false)
        with(binding) {
            title.setText(R.string.str_seeting)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//<21
                btnLogOut.setBackgroundResource(R.drawable.shape_corners_2_primary)
            } else {
                btnLogOut.setBackgroundResource(R.drawable.selector_ripple)
            }
            tvVersion.text = getLocalVersion(context!!)
            btnLogOut.setOnClickListener(this@FifthFragment)
        }

        return binding.root
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_log_out -> showDialog()
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        var dialogViewBinding: LogOutDialogViewBinding =
            DataBindingUtil.inflate<LogOutDialogViewBinding>(
                LayoutInflater.from(context), R.layout.log_out_dialog_view,
                null, false
            ).also {
                it.btCancel.setOnClickListener { dialog.dismiss() }
                it.btEnsure.setOnClickListener {
                    islogin = false
                    LoginActivity.start(context!!)
                    activity!!.finish()
                }
            }


        dialog.setView(dialogViewBinding.root)
        dialog.show()
        val d = activity!!.windowManager.defaultDisplay
        val p = dialog.window!!.attributes
        p.height = (d.height * 0.25).toInt()
        p.width = (d.width * 0.7).toInt()
        dialog.window!!.attributes = p

    }

    private fun getLocalVersion(ctx: Context): String {
        var localVersion = "0.0"
        try {
            val packageInfo = ctx.applicationContext
                .packageManager
                .getPackageInfo(ctx.packageName, 0)
            localVersion = packageInfo.versionName

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return "V$localVersion"
    }

    companion object {
        fun newInstance(): FifthFragment {
            return FifthFragment()
        }
    }

}