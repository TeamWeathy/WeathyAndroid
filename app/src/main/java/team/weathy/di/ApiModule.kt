package team.weathy.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import team.weathy.api.ApiFactory
import team.weathy.api.AuthAPI
import team.weathy.api.CalendarAPI
import team.weathy.api.ClothesAPI
import team.weathy.api.UserAPI
import team.weathy.api.WeatherAPI
import team.weathy.api.WeathyAPI
import team.weathy.api.mock.MockCalendarAPI
import team.weathy.api.mock.MockUserAPI
import team.weathy.api.mock.MockWeatherAPI
import team.weathy.api.mock.MockWeathyAPI
import team.weathy.util.UniqueIdentifier
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.BINARY

@Qualifier
@Retention(BINARY)
annotation class Api

@Qualifier
@Retention(BINARY)
annotation class ApiMock

@Module
@InstallIn(ApplicationComponent::class)
class ApiModule {
    @Provides
    @Singleton
    fun provideRetrofitProvider(uniqueId: UniqueIdentifier) = ApiFactory(uniqueId)

    @Provides
    @Singleton
    @Api
    fun provideAuth(provider: ApiFactory) = provider.createApi(AuthAPI::class)

    @Provides
    @Singleton
    @Api
    fun provideCalendar(provider: ApiFactory) = provider.createApi(CalendarAPI::class)

    @Provides
    @Singleton
    @Api
    fun provideClothes(provider: ApiFactory) = provider.createApi(ClothesAPI::class)

    @Provides
    @Singleton
    @Api
    fun provideUser(provider: ApiFactory) = provider.createApi(UserAPI::class)

    @Provides
    @Singleton
    @Api
    fun provideWeather(provider: ApiFactory) = provider.createApi(WeatherAPI::class)

    @Provides
    @Singleton
    @Api
    fun provideWeahy(provider: ApiFactory) = provider.createApi(WeathyAPI::class)
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class ApiModuleMock {
    @Singleton
    @Binds
    @ApiMock
    abstract fun bindUser(api: MockUserAPI): UserAPI

    @Singleton
    @Binds
    @ApiMock
    abstract fun bindCalendar(api: MockCalendarAPI): CalendarAPI

    @Singleton
    @Binds
    @ApiMock
    abstract fun bindWeather(api: MockWeatherAPI): WeatherAPI

    @Singleton
    @Binds
    @ApiMock
    abstract fun bindWeathy(api: MockWeathyAPI): WeathyAPI
}

