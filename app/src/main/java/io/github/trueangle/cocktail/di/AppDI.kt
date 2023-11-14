package io.github.trueangle.cocktail.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.trueangle.cocktail.data.DrinkRepositoryImpl
import io.github.trueangle.cocktail.data.DrinksApi
import io.github.trueangle.cocktail.data.db.DrinksDatabase
import io.github.trueangle.cocktail.domain.repository.DrinkRepository
import io.github.trueangle.cocktail.ui.AppViewModelFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

class AppComponentDependencies(val appContext: Context)

@Singleton
@Component(
    modules = [AppModule::class, NetworkModule::class],
    dependencies = [AppComponentDependencies::class]
)
interface AppComponent {
    val drinkRepository: DrinkRepository
    val appViewModelFactory: AppViewModelFactory

    companion object {
        fun init(dependencies: AppComponentDependencies): AppComponent =
            DaggerAppComponent
                .builder()
                .appComponentDependencies(dependencies)
                .build()
    }
}

@Module
interface AppModule {
    @[Binds Singleton]
    fun bindDrinkRepos(impl: DrinkRepositoryImpl): DrinkRepository

    companion object {
        @[Provides Singleton]
        fun provideRoom(context: Context) =
            Room
                .databaseBuilder(context, DrinksDatabase::class.java, "drinks_db")
                .build()

        @[Provides Singleton]
        fun provideDrinkDao(db: DrinksDatabase) = db.drinkDao()
    }
}

@Module
object NetworkModule {
    private const val BASE_URL = "https://www.thecocktaildb.com/api/json/"
    private const val CONTENT_TYPE = "application/json"

    @[Provides Singleton]
    fun retrofitApi(
        okHttpClient: OkHttpClient,
        jsonConverterFactory: Converter.Factory
    ): DrinksApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(jsonConverterFactory)
        .build()
        .create(DrinksApi::class.java)

    @[Provides Singleton]
    fun okHttpClient() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    @[Provides Singleton]
    fun provideJsonConverterFactory(): Converter.Factory =
        Json { ignoreUnknownKeys = true }.asConverterFactory(CONTENT_TYPE.toMediaType())
}


