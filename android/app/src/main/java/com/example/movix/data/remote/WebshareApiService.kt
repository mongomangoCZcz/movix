package com.example.movix.data.remote

import com.example.movix.data.remote.model.*
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WebshareApiService {
    @FormUrlEncoded
    @POST("api/salt/")
    suspend fun getSalt(
        @Field("username_or_email") username: String
    ): WebshareSaltResponse

    @FormUrlEncoded
    @POST("api/login/")
    suspend fun login(
        @Field("username_or_email") username: String,
        @Field("password") passwordHashed: String,
        @Field("digest") digest: String,
        @Field("keep_logged_in") keepLoggedIn: Int = 1
    ): WebshareLoginResponse

    @FormUrlEncoded
    @POST("api/search/")
    suspend fun search(
        @Field("what") query: String,
        @Field("category") category: String = "video",
        @Field("sort") sort: String = "rating",
        @Field("limit") limit: Int = 100
    ): WebshareSearchResponse

    @FormUrlEncoded
    @POST("api/file_link/")
    suspend fun getFileLink(
        @Field("ident") ident: String,
        @Field("wst") token: String
    ): WebshareLinkResponse
}
