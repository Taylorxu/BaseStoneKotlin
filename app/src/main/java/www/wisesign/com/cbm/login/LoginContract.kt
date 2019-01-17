package www.wisesign.com.cbm.login

import www.wisesign.com.cbm.activitys.BasePresenter
import www.wisesign.com.cbm.activitys.BaseView

interface LoginContract {

    interface Presenter : BasePresenter {
        fun doLogin(name: String, passwd: String)

    }

    interface View : BaseView<Presenter> {
        fun onLoginResult(status:Int,result: String)

        fun onSetProgressBarVisibility(visibility: Int)

        fun onViewEnable(b: Boolean)

        fun onSetAccountText(s:String)
    }
}