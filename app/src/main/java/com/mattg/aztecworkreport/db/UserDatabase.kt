package com.mattg.aztecworkreport.db

import android.content.Context
import androidx.room.Database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.mattg.aztecworkreport.models.User


@Database(entities = [User::class], version = 12)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao


    companion object{

        @Volatile private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {

            synchronized(this){

                var instance = INSTANCE

                if(instance == null) instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "recipes.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }

        }



    }
}