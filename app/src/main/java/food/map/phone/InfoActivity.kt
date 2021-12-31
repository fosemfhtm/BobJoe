package food.map.phone

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import food.map.MainActivity
import food.map.R
import food.map.databinding.ActivityInfoBinding
import java.util.*

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = intent.getStringExtra("name")

        binding.tvInfoPhone.text =
            PhoneNumberUtils.formatNumber(intent.getStringExtra("phone"), Locale.getDefault().country)
        binding.tvInfoLoc.text = intent.getStringExtra("loc")

        binding.btnInfoCall.setOnClickListener {
            TedPermission.create()
                .setDeniedMessage("[설정]에서 전화 권한을 허용해주세요!")
                .setPermissions(android.Manifest.permission.CALL_PHONE)
                .setPermissionListener(object : PermissionListener{
                    override fun onPermissionGranted() {
                        startActivity(Intent("android.intent.action.CALL", Uri.parse("tel:${intent.getStringExtra("phone")}")))
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    }

                })
                .check()
        }

        binding.btnInfoLoc.setOnClickListener {
            startActivity(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse("geo:0,0?q=${intent.getStringExtra("loc")}")
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_del -> delPhone()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun delPhone() {
        val pos = intent.getIntExtra("pos", 0)
        val jsonController = JsonController(this)
        val data = jsonController.readFromJson()
        jsonController.updateToJson(Gson().toJson(data.apply {
            removeAt(pos)
        }))

        setResult(RESULT_OK, Intent(this, MainActivity::class.java))
        finish()
    }
}