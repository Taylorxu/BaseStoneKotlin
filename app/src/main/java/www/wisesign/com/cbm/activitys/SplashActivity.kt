package www.wisesign.com.cbm.activitys

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import com.xuzhiguang.xzglibrary.helperTool.PreferenceKeList
import com.xuzhiguang.xzglibrary.helperTool.SharePreference
import org.jetbrains.anko.startActivity
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.login.LoginActivity


class SplashActivity : AppCompatActivity() {
    internal var bundle: Bundle? = null
    @SuppressLint("HandlerLeak")
     var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                -1 -> {
                    bundle = null
                    startActivity<MainActivity>()
                    finish()
                }
                2 -> {
                    LoginActivity.start(this@SplashActivity)
                    finish()
                }}
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkEveryThing()
    }

    private fun checkEveryThing() {
        var isLogInPreference: Boolean by SharePreference.perference(this, PreferenceKeList.isLogIn, false)
        if (isLogInPreference) {
            handler.sendEmptyMessage(-1)
        } else {
            handler.sendEmptyMessage(2)
        }
    }
}
