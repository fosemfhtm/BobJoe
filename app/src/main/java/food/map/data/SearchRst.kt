package food.map.data

import com.google.gson.annotations.SerializedName

data class SearchRst(
    @SerializedName("display")
    val display: Int,
    @SerializedName("items")
    val items: List<Item>,
    @SerializedName("lastBuildDate")
    val lastBuildDate: String,
    @SerializedName("start")
    val start: Int,
    @SerializedName("total")
    val total: Int
) {
    data class Item(
        @SerializedName("bloggerlink")
        val bloggerlink: String,
        @SerializedName("bloggername")
        val bloggername: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("link")
        val link: String,
        @SerializedName("postdate")
        val postdate: String,
        @SerializedName("title")
        val title: String
    )
}