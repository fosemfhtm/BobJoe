package food.map.api

import food.map.data.MapData
import food.map.data.SearchRst
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiInterface {
    @GET("search/{type}")
    fun getSearchResult(
        @Header("X-Naver-Client-Id") id: String?,
        @Header("X-Naver-Client-Secret") pw: String?,
        @Path("type") type: String?,
        @Query("query") query: String?
    ): Call<SearchRst>

    @GET("geocode")
    fun getGeoLocation(
        @Header("X-NCP-APIGW-API-KEY-ID") id: String?,
        @Header("X-NCP-APIGW-API-KEY") pw: String?,
        @Query("query") query: String?
    ): Call<MapData>
}