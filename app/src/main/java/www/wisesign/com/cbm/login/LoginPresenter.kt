package www.wisesign.com.cbm.login

import android.view.View
import com.xuzhiguang.xzglibrary.BaseApplication
import com.xuzhiguang.xzglibrary.helperTool.PreferenceKeList
import com.xuzhiguang.xzglibrary.helperTool.SharePreference
import com.xuzhiguang.xzglibrary.http.FlatMapResponse
import com.xuzhiguang.xzglibrary.http.MySubscriber
import com.xuzhiguang.xzglibrary.http.ResultModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import www.wisesign.com.cbm.model.LoginResponse
import www.wisesign.com.cbm.request.ApiService
import java.util.HashMap

class LoginPresenter(val loginView: LoginContract.View) : LoginContract.Presenter {
    var userAccountPreference: String by SharePreference.perference(
        BaseApplication.instance.baseContext,
        PreferenceKeList.userAccount, ""
    )
    var isLogInPreference: Boolean by SharePreference.perference(
        BaseApplication.instance.baseContext,
        PreferenceKeList.isLogIn,
        true
    )

    var oldNavVisibility: String by SharePreference.perference(
        BaseApplication.instance.baseContext,
        PreferenceKeList.oldNavVisibility,
        "0"
    )

    var newNavVisibility: String by SharePreference.perference(
        BaseApplication.instance.baseContext,
        PreferenceKeList.newNavVisibility,
        "0"
    )

    init {
        loginView.presenter = this
    }

    override fun start() {
        loginView.onSetAccountText(userAccountPreference)
    }

    override fun doLogin(name: String, passwd: String) {
        loginView.onViewEnable(false)
        loginView.onSetProgressBarVisibility(View.VISIBLE)

        val map = mapOf("accountNum" to name, "password" to passwd)
        ApiService.get().userLoginServlet("UserLoginServlet", map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { t -> FlatMapResponse.callResponse(t) }
            .subscribe(object : MySubscriber<LoginResponse>() {
                override fun onNext(response: LoginResponse?) {
                    userAccountPreference = name  //保存用account
                    isLogInPreference = true //保存 登录状态
                    //  保存 新(businessMonitor) and 老(unifiedMonitor)导航按钮显示权限
                    response?.unifiedMonitor?.let {
                        oldNavVisibility = it
                    }
                    response?.businessMonitor?.let {
                        newNavVisibility = it
                    }
                    loginView.onLoginResult(0, response?.Reason!!)
                    loginView.onViewEnable(true)
                    loginView.onSetProgressBarVisibility(View.GONE)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    loginView.onViewEnable(true)
                    loginView.onLoginResult(1, "")
                    loginView.onSetProgressBarVisibility(View.GONE)
                }
            })


    }


}
