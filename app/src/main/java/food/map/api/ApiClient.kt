package food.map.api

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import com.google.gson.GsonBuilder

object ApiClient {
    private const val BASE_URL = "https://openapi.naver.com/v1/"
    private var retrofit: Retrofit? = null
    val instance: Retrofit?
        get() {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit
        }
}