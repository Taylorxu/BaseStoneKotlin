package www.wisesign.com.cbm.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.WindowManager
import com.xuzhiguang.xzglibrary.helperTool.replaceFragmentInActivity
import www.wisesign.com.cbm.R
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {
    internal var loginPresenter: LoginPresenter by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginFragment =
            supportFragmentManager.findFragmentById(R.id.login_frame_layout) as LoginFragment?
                ?: LoginFragment.newInstance().also {
                    replaceFragmentInActivity(it, R.id.login_frame_layout)
                }
        loginPresenter = LoginPresenter(loginFragment)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.color_login_statue)
        }
    }

    companion object {

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        fun start(context: Context) {
            val starter = Intent(context, LoginActivity::class.java)
            context.startActivity(starter)
        }
    }

}
