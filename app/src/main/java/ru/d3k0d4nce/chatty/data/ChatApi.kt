package ru.d3k0d4nce.chatty.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://faerytea.name/"
const val PAGE_SIZE = 20

data class Message(
    val id: Long = 0L,
    val from: String? = null,
    val data: MessageData? = null,
    val time: Long = 0L
)

data class MessageData(
    @SerializedName("Text") val text: TextContent? = null,
    @SerializedName("Image") val image: ImageContent? = null
)

data class TextContent(val text: String? = null)

data class ImageContent(val link: String? = null)

interface ChatApi {
    @GET("channels")
    suspend fun getChannels(): List<String>

    @GET("channel/{name}")
    suspend fun getMessages(
        @Path("name") name: String,
        @Query("lastKnownId") lastKnownId: Long,
        @Query("limit") limit: Int = PAGE_SIZE,
        @Query("reverse") reverse: Boolean = true
    ): List<Message>
}

val chatApi: ChatApi by lazy {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatApi::class.java)
}
