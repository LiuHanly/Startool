package com.glorystar.startool.utils.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.glorystar.startool.config.base.BaseDao


@Dao
interface TeacherDao : BaseDao<Teacher> {

    @Insert
    fun insert(element: Teacher)

    @Query("delete from Teacher")
    fun deleteAll()


    @Query("select * from Teacher")
    fun getAllTeachers(): MutableList<Teacher>

    @Query("select * from Teacher where TeacherID IN (:teacherID)")
    fun getTeacherIDList(teacherID: List<Long?>): MutableList<Teacher>


    @Query("select * from Teacher where t_name = :teacherName")
    fun getTeacher(teacherName: String): Teacher

    @Query("select * from Teacher where TeacherID = :id")
    fun getTeacherID(id: Long): Teacher

}