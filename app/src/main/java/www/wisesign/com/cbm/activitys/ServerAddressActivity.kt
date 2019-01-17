package www.wisesign.com.cbm.activitys

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.xuzhiguang.xzglibrary.helperTool.NiceToast
import com.xuzhiguang.xzglibrary.helperTool.PreferenceKeList
import com.xuzhiguang.xzglibrary.helperTool.SharePreference
import www.wisesign.com.cbm.R
import www.wisesign.com.cbm.databinding.ActivityServerAddressBinding
import www.wisesign.com.cbm.login.LoginActivity

class ServerAddressActivity : AppCompatActivity() {
    lateinit var binding: ActivityServerAddressBinding
    var serverUrl: String by SharePreference.perference(this, PreferenceKeList.serverUrl, "172.21.2.10:8080/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_server_address)
        binding.title.text = resources.getString(R.string.str_service_set)
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_back -> finish()
            R.id.bt_server_save -> saveAddress()
        }

    }

    private fun saveAddress() {
        binding.etServerUrl.text.toString().trim().also {
            if (TextUtils.isEmpty(it)) {
                Toast.makeText(this, resources.getString(R.string.toast_server), Toast.LENGTH_SHORT).show()
                return
            } else if (!it.endsWith("/")) {
                Toast.makeText(this, resources.getString(R.string.toast_server_end), Toast.LENGTH_SHORT).show()
                return
            }
            serverUrl = it
            binding.etServerUrl.setText("")
            NiceToast.toast(resources.getString(R.string.toast_server_save))
            LoginActivity.start(this@ServerAddressActivity)
            finish()
        }

    }


    companion object {

        fun start(context: Context) {
            val starter = Intent(context, ServerAddressActivity::class.java)
            context.startActivity(starter)
        }
    }
}
