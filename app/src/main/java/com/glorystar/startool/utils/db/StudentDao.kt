package com.glorystar.startool.utils.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.glorystar.startool.config.base.BaseDao

@Dao
interface StudentDao : BaseDao<Student> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: Student)

    @Query("select * from Student")
    fun getAllStudents(): MutableList<Student>

    @Query("select * from Student where StudentID = :studentID")
    fun getStudnetID(studentID: Int): Student


    @Query("select * from Student where s_name = :studentNeme")
    fun getStudnetName(studentNeme: String): Student


    @Query("delete from Student")
    fun deleteAll()



}