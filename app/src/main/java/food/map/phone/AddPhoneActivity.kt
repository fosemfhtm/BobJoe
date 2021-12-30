package food.map.phone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.gson.Gson
import food.map.MainActivity
import food.map.data.PhonePage
import food.map.databinding.ActivityAddPhoneBinding

class AddPhoneActivity : Activity() {
    private lateinit var binding: ActivityAddPhoneBinding
    private lateinit var data: ArrayList<PhonePage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = ActivityAddPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        window.attributes.width = (display.width * 0.7).toInt()
        window.attributes.height = (display.height * 0.7).toInt()

        val jsonController = JsonController(this)
        data = jsonController.readFromJson()

        binding.btnOk.setOnClickListener {
            val newData = PhonePage(
                binding.etName.text.toString(),
                binding.etPhone.text.toString(),
                binding.etLocation.text.toString()
            )
            jsonController.updateToJson(Gson().toJson(data.apply { add(0, newData) }))
            Toast.makeText(this, "연락처가 추가되었습니다!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK, Intent(this,MainActivity::class.java))
            finish()
        }
    }
}