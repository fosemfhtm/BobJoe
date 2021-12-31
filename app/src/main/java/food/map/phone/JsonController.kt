package food.map.phone

import android.content.Context
import com.google.gson.Gson
import food.map.data.PhonePage
import java.io.*

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
}