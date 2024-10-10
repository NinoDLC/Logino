package fr.delcey.logino.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.delcey.logino.data.home.HomeRepositoryRetrofit
import fr.delcey.logino.domain.home.HomeRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindModule {

    @Singleton
    @Binds
    abstract fun bindHomeRepository(impl: HomeRepositoryRetrofit): HomeRepository
}