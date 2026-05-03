package com.cardinal.data.local.di

import android.content.Context
import androidx.room.Room
import com.cardinal.data.local.db.CardinalDatabase
import com.cardinal.data.local.db.DrivenSegmentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CardinalDatabase {
        return Room.databaseBuilder(
            context,
            CardinalDatabase::class.java,
            "cardinal.db"
        ).build()
    }

    @Provides
    fun provideDrivenSegmentDao(db: CardinalDatabase): DrivenSegmentDao = db.drivenSegmentDao()
}
