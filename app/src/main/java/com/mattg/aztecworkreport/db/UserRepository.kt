package com.mattg.aztecworkreport.db


import androidx.lifecycle.LiveData
import com.mattg.aztecworkreport.models.User


class UserRepository(private val dao: UserDao) {


    suspend fun getUserByName(name: String): User {
        return dao.getUserByName(name)
    }

    fun addUser(user: User) {
        dao.addUser(user)
    }

    fun getUserLiveData(name: String): LiveData<User> {
        return dao.getUserLiveByName(name)
    }

    fun getPiecesLiveData(name: String): LiveData<Int> {
        return dao.loadPieces(name)
    }

    fun getHousesLiveData(name: String): LiveData<Int> {
        return dao.loadHouses(name)
    }

    suspend fun checkUser(user: User): User {
        return dao.getUserByName(user.userName.toString())
    }

    fun doesUserExist(name: String): Boolean {
        return dao.isRowIsExist(name)
    }



    suspend fun setStartTime(time: String, name: String) {
        dao.setStartTime(time, name)
    }

    suspend fun setTotalMinutes(minutes: String, name: String) {
        dao.setTotalMinutes(minutes, name)
    }

    suspend fun setTotalHours(hours: String, name: String) {
        dao.setTotalHours(hours, name)
    }

    suspend fun setFinishTime(time: String, name: String) {
        dao.setFinishTime(time, name)
    }

    suspend fun setTotalTime(time: String, name: String) {
        dao.setTotalTime(time, name)
    }

    suspend fun setHouseNumber(houses: Int, name: String) {
        dao.setHouses(houses, name)
    }

    suspend fun setPiecesNumber(pieces: Int, name: String) {
        dao.setPieces(pieces, name)
    }

    suspend fun setSuperNumber(number: String, name: String) {
        dao.setSuperNumber(number, name)
    }


    suspend fun setResultStats(data: String, name: String){
        dao.setResultStats(data, name)
    }

    fun getResultStats(name: String) : LiveData<String> {
        return dao.getResultStats(name)
    }
   fun getResultStatsString(name: String) : String {
        return dao.getResultStatsString(name)
    }

    fun setIsStarted(bool: Boolean, name: String){
        dao.setIsStarted(bool, name)
    }

    fun getIsStarted(name: String): LiveData<Boolean> {
        return dao.getIsStartedResult(name)
    }
}