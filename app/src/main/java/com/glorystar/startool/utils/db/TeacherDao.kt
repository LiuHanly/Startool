package com.glorystar.systemtest.utlis.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.glorystar.startool.config.base.BaseDao


@Dao
interface TeacherDao : BaseDao<Teacher> {

    @Insert
    fun insert(element: Teacher)

    @Query("delete from Teacher")
    fun deleteAll()


    @Query("select * from Teacher")
    fun getAllTeachers(): MutableList<Teacher>

    @Query("select * from Teacher where t_name = :teacherName")
    fun getTeacher(teacherName: String): Teacher

    @Query("select * from Teacher where TeacherID = :id")
    fun getTeacherID(id: Long): Teacher

}