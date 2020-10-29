package com.mattg.aztecworkreport.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo
    val userName: String?,
    @ColumnInfo
    val pin: Int?,
    @ColumnInfo
    var housesDone: Int?,
    @ColumnInfo
    var piecesDone: Int?,
    @ColumnInfo
    var currentJobStartTime: String?,
    @ColumnInfo
    var currentJobFinishTime: String?,
    @ColumnInfo
    var totalTime: String?,
    @ColumnInfo
    var totalHours: String?,
    @ColumnInfo
    var totalMinutes: String?,
    @ColumnInfo
    var superNumber: String?,
    @ColumnInfo
    val resultStats: String?,
    @ColumnInfo
    var isStarted: Boolean?


){
    @Ignore
    constructor(userName: String?, pin: Int?, number: String?) :
            this(
                0,
                userName,
                pin,
                0,
                0,
                "",
                "",
                "",
                "",
                "",

                number,
                "",
                false

            )

    @Ignore
    constructor(userName: String?, pin: Int?) :
            this(
                0,
                userName,
                pin,
                0,
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                false

            )

}