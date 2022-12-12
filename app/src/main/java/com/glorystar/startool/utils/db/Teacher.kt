package com.glorystar.systemtest.utlis.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Teacher")
data class Teacher(
    @PrimaryKey(autoGenerate = true)
    var TeacherID: Long?,
    @ColumnInfo(name = "t_name")
    var teacherName: String?,
    @ColumnInfo(name = "statr_date")
    var statrDate: String?,
    @ColumnInfo(name = "stop_date")
    var stopDate: String?,
    @ColumnInfo(name ="is_type")
    var TeacherType: Int

)
