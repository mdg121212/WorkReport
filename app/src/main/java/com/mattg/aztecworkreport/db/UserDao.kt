package com.mattg.aztecworkreport.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mattg.aztecworkreport.models.User


@Dao
interface UserDao {


    @Insert
    fun addUser(user: User)

    @Delete
    fun deleteUser(user: User)
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User

    @Query("SELECT COUNT(id) FROM users")
    suspend fun getAllUsers(): Int

    @Query("SELECT * FROM users WHERE userName = :name")
    suspend fun getUserByName(name: String): User

    @Query("SELECT currentJobStartTime FROM users WHERE userName = :name")
    suspend fun getStartTime(name: String) : String

    @Query("SELECT currentJobFinishTime FROM users WHERE userName = :name")
    suspend fun getEndTime(name: String) : String

    @Query("SELECT EXISTS(SELECT * FROM users WHERE userName = :id)")
    fun isRowIsExist(id : String): Boolean

    @Query("SELECT * FROM users WHERE userName = :name")
    fun getUserLiveByName(name: String): LiveData<User>

    @Update
    fun updateUser(user: User)
    //to load saved pieces to viewmodel
    @Query("SELECT piecesDone FROM users where userName = :name")
    fun loadPieces(name: String): LiveData<Int>
    @Query("SELECT housesDone FROM users where userName = :name")
    fun loadHouses(name: String): LiveData<Int>


    @Query("UPDATE users SET currentJobStartTime = :time WHERE userName = :name")
    suspend fun setStartTime(time: String, name: String)

    @Query("UPDATE users SET currentJobFinishTime = :time WHERE userName = :name")
    suspend fun setFinishTime(time: String, name: String)

    @Query("UPDATE users SET piecesDone = :pieces WHERE userName = :name")
    suspend fun setPieces(pieces: Int, name: String)

    @Query("UPDATE users SET housesDone = :houses WHERE userName = :name")
    suspend fun setHouses(houses: Int, name: String)

    @Query("UPDATE users SET totalTime = :totalTime WHERE userName = :name")
    suspend fun setTotalTime(totalTime: String, name: String)

    @Query("UPDATE users SET totalHours = :hours WHERE userName = :name")
    suspend fun setTotalHours(hours: String, name: String)

    @Query("UPDATE users SET totalMinutes = :minutes WHERE userName = :name")
    suspend fun setTotalMinutes(minutes: String, name: String)


    //to set supervisor number to send texts to
    @Query("UPDATE users SET superNumber = :number WHERE userName = :name")
    suspend fun setSuperNumber(number: String, name: String)

    //to get and set the string that holds statitics data
    @Query("UPDATE users SET resultStats = :newData WHERE userName = :name")
    suspend fun setResultStats(newData: String, name: String)

    @Query("SELECT resultStats FROM users WHERE userName = :name")
     fun getResultStats(name: String): LiveData<String>

    @Query("SELECT resultStats FROM users WHERE userName = :name")
    fun getResultStatsString(name: String): String

    @Query("UPDATE users SET isStarted = :bool WHERE userName = :name")
    fun setIsStarted(bool: Boolean, name: String)

    @Query("SELECT isStarted FROM users WHERE userName = :name")
    fun getIsStartedResult(name: String): LiveData<Boolean>
}