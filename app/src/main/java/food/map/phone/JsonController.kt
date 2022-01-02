package food.map.phone

import android.content.Context
import com.google.gson.Gson
import food.map.api.ApiClient
import food.map.api.ApiInterface
import food.map.data.MapData
import food.map.data.PhonePage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*


const val MAP_KEY_ID = "1zlm7yez4o"
const val MAP_KEY_SECRET = "kkMeFVRilYjUBsdypx5jsIxIHlmGYQVtE9Me1dxR"

class JsonController(private val context: Context) {
    private fun makeNewJson(file: File) {
        val assetManager = context.resources.assets
        val inputStream = assetManager.open("phonebook.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val fileWriter = FileWriter(file, false)
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.append(jsonString)
        bufferedWriter.close()
    }

    private fun getJsonString(file: File): String {
        var jsonString = ""
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)
        for (readLine in bufferedReader.readLines()) {
            jsonString += readLine
        }
        return jsonString
    }

    fun readFromJson(): ArrayList<PhonePage> {
        val filePath = context.getExternalFilesDir(null)!!.path + "/phonebook.json"

        val file = File(filePath)
        if (!file.exists()) makeNewJson(file) //Create new if there is no one
        val jsonString = getJsonString(file) //Read saved json file

        //JsonArray -> List (Gson)
        val list: List<PhonePage> =
            Gson().fromJson(jsonString, Array<PhonePage>::class.java).toList()

        val arrayList = arrayListOf<PhonePage>()
        arrayList.addAll(list)
        return arrayList
    }

    fun updateToJson(jsonString: String) {
        val filePath = context.getExternalFilesDir(null)!!.path + "/phonebook.json"

        val file = File(filePath)
        file.delete()

        val newFile = File(filePath)
        val fileWriter = FileWriter(newFile, false)
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.append(jsonString)
        bufferedWriter.close()
    }

    suspend fun addressToGeoCode(address: String): MapData? {
        val apiInterface = ApiClient("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/")
            .instance?.create(ApiInterface::class.java)
        val call = apiInterface?.getGeoLocation(MAP_KEY_ID, MAP_KEY_SECRET, address)
        var mapData: MapData? = null

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                mapData = call?.execute()?.body()
            }
        }.join()
        return mapData
    }
}