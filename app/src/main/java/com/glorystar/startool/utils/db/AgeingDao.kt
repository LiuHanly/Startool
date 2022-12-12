package com.glorystar.startool.utils.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.glorystar.startool.config.base.BaseDao
import com.glorystar.startool.utils.db.Ageing


@Dao
interface AgeingDao : BaseDao<Ageing> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: Ageing)

    @Query("select * from Ageing")
    fun getAllAgeing(): MutableList<Ageing>

    @Query("select * from Ageing where AgeingID = :ageingID")
    fun getAgeingID(ageingID: Int): Ageing


    @Query("select * from Ageing where A_name =:ageingName")
    fun getAgeingName(ageingName: String): Ageing

    @Query("delete from Ageing")
    fun deleteAll()

}