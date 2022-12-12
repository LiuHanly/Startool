package com.glorystar.startool.utils.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Ageing")
data class Ageing(
    @PrimaryKey(autoGenerate = true)
    var AgeingID: Int?,
    @ColumnInfo(name = "A_name")
    var AgeingName: String?,
    @ColumnInfo(name = "Star_time")
    var AgeingStarTime: String?,
    @ColumnInfo(name = "Stop_time")
    var AgeungStopTime: String?,
    @ColumnInfo(name = "A_Type")
    var AgeingType: String?,
    @ColumnInfo(name = "Remaining_time")
    var AgeingRemaining: String?

    )
