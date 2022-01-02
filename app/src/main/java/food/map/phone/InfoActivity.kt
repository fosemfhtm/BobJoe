package food.map.phone

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import food.map.MainActivity
import food.map.R
import food.map.api.ApiClient
import food.map.api.ApiInterface
import food.map.data.SearchRst
import food.map.databinding.ActivityInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

const val CLIENT_ID = "KrmmtFDWUFpeCnNH1Q0J"
const val CLIENT_SECRET = "wRvBXc6v6D"

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("name")
        supportActionBar?.title = title

        getSearchRst(title!!)

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

    private fun getSearchRst(query: String) {
        val apiInterface = ApiClient("https://openapi.naver.com/v1/").instance?.create(ApiInterface::class.java)
        val call = apiInterface?.getSearchResult(CLIENT_ID, CLIENT_SECRET, "blog.json", query)

        call?.enqueue(object : Callback<SearchRst> {
            override fun onResponse(call: Call<SearchRst>, response: Response<SearchRst>) {
                val list = response.takeIf { it.isSuccessful }?.body()!!.items
                val arrayList = arrayListOf<SearchRst.Item>()
                arrayList.addAll(list)

                binding.rvSearch.apply {
                    adapter = SearchAdapter(this@InfoActivity, arrayList, layoutInflater)
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(context, 1))
                }
            }

            override fun onFailure(call: Call<SearchRst>, t: Throwable) {
                Log.e("err", t.message.toString())
            }
        })
    }
}

class SearchAdapter(val context: Context, var itemList: ArrayList<SearchRst.Item>, private val inflater: LayoutInflater):
        RecyclerView.Adapter<SearchAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.tv_info_title)
        val desc: TextView = itemView.findViewById(R.id.tv_info_desc)
        val date: TextView = itemView.findViewById(R.id.tv_info_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.rv_item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = itemList[position].title.replace("(?s)<[^>]*>(\\s*<[^>]*>)*".toRegex(), " ")
        holder.desc.text = itemList[position].description.replace("(?s)<[^>]*>(\\s*<[^>]*>)*".toRegex(), " ")

        val date = itemList[position].postdate
        holder.date.text = "${date.slice(IntRange(0,3))}-${date.slice(IntRange(4,5))}-${date.slice(
            IntRange(6,7)
        )}"

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(itemList[position].link) }
            )
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}










