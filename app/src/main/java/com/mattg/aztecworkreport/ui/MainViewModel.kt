package com.mattg.aztecworkreport.ui



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mattg.aztecworkreport.models.User
import com.mattg.aztecworkreport.db.UserRepository


import kotlin.math.ceil


class MainViewModel(private val userRepository: UserRepository, name: String) : ViewModel() {


    val retrievedUser: LiveData<User> = userRepository.getUserLiveData(name)
    val retrievedPieces: LiveData<Int> = userRepository.getPiecesLiveData(name)
    val retrievedHouses: LiveData<Int> = userRepository.getHousesLiveData(name)
    val retrievedStats: LiveData<String> = userRepository.getResultStats(name)
    val retrievedIsStarted: LiveData<Boolean> = userRepository.getIsStarted(name)

    fun isStarted(bool: Boolean, name: String) {
        userRepository.setIsStarted(bool, name)

    }

    suspend fun setSuperNumber(number: String, name: String) {
        val newStringNumber = "+1$number"
        userRepository.setSuperNumber(newStringNumber, name)
    }

    fun splitTimes(time: String) : List<String>{
        return time.split(':')
    }



    var hoursTotal: Int = 0
    var minutesTotal: Int = 0
    var piecesTotal: Int = 0


    fun getPiecesPerHour(): Int {

        val hoursfromMinutes = minutesTotal / 60
        var leftOver = minutesTotal % 60
        leftOver = if (leftOver < 30) {
            0
        } else
            1

        val total = (hoursTotal + hoursfromMinutes + leftOver).toDouble()
        Log.d(
            "TRIAL",
            "rounded: $total , hours: $hoursTotal, hoursFromMinutes: $hoursfromMinutes minutes: $minutesTotal, leftover: $leftOver, total: $total, pieces: $piecesTotal"
        )
        //get a rounded up integer for pieces per hour
        val ratio = (ceil((piecesTotal.toDouble()) / total)).toInt()
        Log.d("TRIAL", "PIECE PER HOUR = $ratio")
        return (ratio)

    }

    private suspend fun clearResults(data: String, name: String) {
        userRepository.setResultStats(data, name)
    }

    suspend fun setResultStats(data: String, name: String) {

        val statsToAddTo = userRepository.getResultStatsString(name)

        val statsToAdd = "$statsToAddTo \n $data"

        userRepository.setResultStats(statsToAdd, name)
    }

    suspend fun userStartTime(time: String, name: String) {
        userRepository.setStartTime(time, name)
    }

    suspend fun userFinishTime(time: String, name: String) {
        userRepository.setFinishTime(time, name)
    }

    private suspend fun userTotalTime(time: String, name: String) {
        userRepository.setTotalTime(time, name)
    }

    suspend fun userHouseNumber(houses: Int, name: String) {
        userRepository.setHouseNumber(houses, name)
    }

    suspend fun userPiecesNumber(pieces: Int, name: String) {
        userRepository.setPiecesNumber(pieces, name)
    }

    suspend fun clearStats(name: String) {
        userPiecesNumber(0, name)
        userHouseNumber(0, name)
        userStartTime("", name)
        userFinishTime("", name)
        userTotalTime("", name)
        clearResults("", name)
        isStarted(false, name)
    }

    suspend fun saveAllStats(name: String) {
        userPiecesNumber(retrievedUser.value!!.piecesDone!!.toInt(), name)
        userHouseNumber(retrievedUser.value!!.housesDone!!.toInt(), name)
        userFinishTime(retrievedUser.value!!.currentJobFinishTime!!, name)
        userStartTime(retrievedUser.value!!.currentJobStartTime!!, name)

    }


}