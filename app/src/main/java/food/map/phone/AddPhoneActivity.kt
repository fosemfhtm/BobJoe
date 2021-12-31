package food.map.phone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import food.map.MainActivity
import food.map.R
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
        window.attributes.width = (display.width * 0.8).toInt()
        window.attributes.height = (display.height * 0.7).toInt()

        val jsonController = JsonController(this)
        data = jsonController.readFromJson()

        binding.spinnerType.adapter = ArrayAdapter.createFromResource(
            this, R.array.food_type, android.R.layout.simple_spinner_item
        )

        binding.etPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        binding.btnOk.setOnClickListener {
            val name = binding.etName.text.toString()
            val phone = binding.etPhone.text.toString()
            val loc = binding.etLocation.text.toString()

            if (name.isNotBlank() && phone.isNotBlank() && loc.isNotBlank()){
                val newData = PhonePage(
                    binding.etName.text.toString(),
                    binding.etPhone.text.toString(),
                    binding.etLocation.text.toString(),
                    binding.spinnerType.selectedItemPosition
                )
                jsonController.updateToJson(Gson().toJson(data.apply { add(newData) }))
                setResult(RESULT_OK, Intent(this,MainActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, "빈칸 없이 모두 입력해주세요!", Toast.LENGTH_SHORT).show()
            }

        }
    }
}