package com.example.movix.di

import com.example.movix.data.remote.TmdbApiService
import com.example.movix.data.remote.VpsApiService
import com.example.movix.data.remote.WebshareApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @Named("TmdbRetrofit")
    fun provideTmdbRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @Named("WebshareRetrofit")
    fun provideWebshareRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val tikXml = TikXml.Builder()
            .exceptionOnUnreadXml(false)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://webshare.cz/")
            .client(okHttpClient)
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .build()
    }

    @Provides
    @Singleton
    @Named("VpsRetrofit")
    fun provideVpsRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://176.102.65.44:5000/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideTmdbApi(@Named("TmdbRetrofit") retrofit: Retrofit): TmdbApiService =
        retrofit.create(TmdbApiService::class.java)

    @Provides
    @Singleton
    fun provideWebshareApi(@Named("WebshareRetrofit") retrofit: Retrofit): WebshareApiService =
        retrofit.create(WebshareApiService::class.java)

    @Provides
    @Singleton
    fun provideVpsApi(@Named("VpsRetrofit") retrofit: Retrofit): VpsApiService =
        retrofit.create(VpsApiService::class.java)
}
