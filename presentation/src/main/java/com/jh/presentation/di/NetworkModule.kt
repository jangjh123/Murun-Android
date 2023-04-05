package com.jh.presentation.di

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.jh.murun.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): ApiService {
        return Retrofit.Builder()
            .baseUrl("http://54.173.222.144:8080/")
            .client(
                OkHttpClient
                    .Builder()
                    .readTimeout(15, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addNetworkInterceptor(
                        HttpLoggingInterceptor { message ->
                            val tag = "ResponseBody"
                            if (message.startsWith("{") || message.startsWith("[")) {
                                try {
                                    val prettyPrintJson = GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(JsonParser().parse(message))
                                    Log.d(tag, prettyPrintJson)
                                } catch (m: JsonSyntaxException) {
                                    Log.d(tag, message)
                                }
                            } else {
                                Log.d(tag, message)
                            }
                        }
                    )
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}