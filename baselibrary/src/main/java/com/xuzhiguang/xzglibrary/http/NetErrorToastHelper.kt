package com.xuzhiguang.xzglibrary.http

import android.widget.Toast
import com.xuzhiguang.xzglibrary.BaseApplication

/**
 * Created by Administrator on 2018/3/16.
 */

class NetErrorToastHelper {

    fun selectWitch(exception: String) {
        if (exception.indexOf("Network is unreachable") > -1) {
            toastMesg("请检查网络连接情况")
            return
        }
        if (exception.indexOf("Failed to connect") > -1) {
            toastMesg("服务连接失败")
            return
        } else if (exception.indexOf("timeout") > -1) {
            toastMesg("连接超时")
            return
        } else if (exception.indexOf("Unable to resolve host") > -1) {
            toastMesg("服务地址错误")
            return
        } else {
            toastMesg(exception)
        }
    }

    fun toastMesg(msg: String) {
        Toast.makeText(BaseApplication.instance.baseContext, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {

        fun newInstance(): NetErrorToastHelper {
            return NetErrorToastHelper()
        }
    }
}
