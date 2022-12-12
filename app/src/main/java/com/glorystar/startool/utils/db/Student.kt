package com.glorystar.startool.utils.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Student")
data class Student(
    @PrimaryKey(autoGenerate = true)
    var StudentID: Int?,
    @ColumnInfo(name = "s_name")
    var StudentName: String?,
    @ColumnInfo(name = "s_type")
    var StudentType: Boolean


)