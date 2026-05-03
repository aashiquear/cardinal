package com.cardinal.core.data.di

import com.cardinal.core.data.DrivenRoadRepository
import com.cardinal.core.data.GradeRepository
import com.cardinal.core.data.PoiRepository
import com.cardinal.core.data.RouteRepository
import com.cardinal.core.data.SearchRepository
import com.cardinal.core.data.SpeedLimitRepository
import com.cardinal.core.data.repository.DrivenRoadRepositoryImpl
import com.cardinal.core.data.repository.GradeRepositoryImpl
import com.cardinal.core.data.repository.PoiRepositoryImpl
import com.cardinal.core.data.repository.RouteRepositoryImpl
import com.cardinal.core.data.repository.SearchRepositoryImpl
import com.cardinal.core.data.repository.SpeedLimitRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        impl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(
        impl: RouteRepositoryImpl
    ): RouteRepository

    @Binds
    @Singleton
    abstract fun bindSpeedLimitRepository(
        impl: SpeedLimitRepositoryImpl
    ): SpeedLimitRepository

    @Binds
    @Singleton
    abstract fun bindPoiRepository(
        impl: PoiRepositoryImpl
    ): PoiRepository

    @Binds
    @Singleton
    abstract fun bindGradeRepository(
        impl: GradeRepositoryImpl
    ): GradeRepository

    @Binds
    @Singleton
    abstract fun bindDrivenRoadRepository(
        impl: DrivenRoadRepositoryImpl
    ): DrivenRoadRepository
}
