package com.batuhan.interviewself.di

import android.content.Context
import androidx.room.Room
import com.batuhan.interviewself.db.InterviewselfDatabase
import com.batuhan.interviewself.db.QuestionConverter
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
    fun provideDatabase(@ApplicationContext context: Context): InterviewselfDatabase {
        return Room.databaseBuilder(context, InterviewselfDatabase::class.java, "interviewself").addTypeConverter(QuestionConverter()).build()
    }
}