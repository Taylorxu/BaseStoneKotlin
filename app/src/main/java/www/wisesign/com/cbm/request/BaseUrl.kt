package www.wisesign.com.cbm.request


import android.text.TextUtils
import com.xuzhiguang.xzglibrary.BaseApplication
import com.xuzhiguang.xzglibrary.helperTool.PreferenceKeList
import com.xuzhiguang.xzglibrary.helperTool.SharePreference

object BaseUrl {
    val DEBUG = false

    var serverUrl: String by SharePreference.perference(
        BaseApplication.instance.baseContext,
        PreferenceKeList.serverUrl,
        "172.21.2.10:8080/"
    )
    var host: String = ""
        get() {
            if (serverUrl.indexOf("MCBM") < 0) {
                serverUrl += "MCBM/"
            }
            return if (serverUrl.indexOf("http://") > -1 || serverUrl.indexOf("HTTP://") > -1) {
                serverUrl
            } else {
                "http://$serverUrl"
            }
        }
}
