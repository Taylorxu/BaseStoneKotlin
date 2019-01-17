package www.wisesign.com.cbm.login


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.view.inputmethod.EditorInfo
import com.xuzhiguang.xzglibrary.helperTool.NiceToast

import www.wisesign.com.cbm.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import org.jetbrains.anko.startActivity
import www.wisesign.com.cbm.activitys.MainActivity
import www.wisesign.com.cbm.activitys.ServerAddressActivity

class LoginFragment : Fragment(), LoginContract.View, View.OnClickListener {
    override lateinit var presenter: LoginContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        with(rootView) {
            sign_in_button.setOnClickListener(this@LoginFragment)
            tv_net_url.setOnClickListener(this@LoginFragment)
            password.setOnEditorActionListener { _, id, _ ->
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    presenter.doLogin(account.text.toString(), password.text.toString())
                    true
                } else {
                    false
                }
            }
        }
        return rootView
    }


    override fun onLoginResult(status: Int, result: String) {
        if (status == 0) {
            NiceToast.toast(getString(R.string.login_sucess_tost))
            activity?.startActivity<MainActivity>()
            activity?.finish()
        } else {
            NiceToast.toast(result)
        }
    }

    override fun onSetProgressBarVisibility(visibility: Int) {
        login_progress.visibility = visibility
    }

    override fun onViewEnable(b: Boolean) {
        sign_in_button.isEnabled = b
        account.isEnabled = b
        password.isEnabled = b
    }

    override fun onSetAccountText(s: String) {
        //从Perference中获取登录人的名字
        account.setText(s)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.sign_in_button -> {
                presenter.doLogin(account.text.toString(), password.text.toString())
            }
            R.id.tv_net_url -> context?.let { ServerAddressActivity.start(it) }
        }
    }

    companion object {
        fun newInstance() = LoginFragment()
    }

}
