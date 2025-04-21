package com.glorystar.startool.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.glorystar.startool.R
import com.glorystar.startool.config.base.BaseActivity
import com.glorystar.startool.config.bean.GridItem
import com.glorystar.startool.config.constant.Constantdtas.writeSDFile
import com.glorystar.startool.config.constant.StudentDataBase
import com.glorystar.startool.config.constant.TeacherDataBase
import com.glorystar.startool.ui.adapter.BaseRecyclerAdapter
import com.glorystar.startool.ui.adapter.BaseRecyclerHolder
import com.glorystar.startool.utils.db.AppDataBase
import com.glorystar.startool.utils.db.Teacher
import com.glorystar.startool.utils.db.TeacherDao
import com.glorystar.startool.utils.tool.DateUtil
import com.glorystar.startool.utils.tool.NetworkUtil
import com.glorystar.startool.utils.tool.StatusNaviBarUitl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal

//测试报告

class TestReportActivity : BaseActivity() {
    lateinit var TerDao: TeacherDao
    lateinit var baserecycleradapter: BaseRecyclerAdapter<Teacher>
    lateinit var listGridItem: MutableList<GridItem>
    var mstoragemanager: StorageManager? = null
    private var mAm: ActivityManager? = null
    lateinit var Serial_number4: TextView

    // lateinit var textView2: TextView
    lateinit var finish_buton: Button
    lateinit var recyclerView: RecyclerView
    lateinit var Serial_number6: TextView
    lateinit var Serial_number7: TextView
    lateinit var Serial_number: TextView
    lateinit var Serial_number1: TextView
    lateinit var Serial_number2: TextView
    lateinit var Serial_number3: TextView
    lateinit var Serial_number5: TextView
    lateinit var Inter_shared_storage: TextView


    companion object {
        // 跳转到当前页面。
        fun toStartActivity(context: Activity) {
            val intent = Intent(context, TestReportActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        StatusNaviBarUitl.show(this@TestReportActivity)
    }

    override fun getLayoutId(): Int = R.layout.activity_test_report

    @SuppressLint("LogNotTimber")
    override fun initView(savedInstanceState: Bundle?) {
        //  textView2 = findViewById(R.id.textView2)
        recyclerView = findViewById(R.id.recyclerView)
        Serial_number6 = findViewById(R.id.Serial_number6)
        Serial_number7 = findViewById(R.id.Serial_number7)
        Serial_number = findViewById(R.id.Serial_number)
        Serial_number1 = findViewById(R.id.Serial_number1)
        Serial_number2 = findViewById(R.id.Serial_number2)
        Serial_number3 = findViewById(R.id.Serial_number3)
        Serial_number5 = findViewById(R.id.Serial_number5)
        finish_buton = findViewById(R.id.finish_buton)
        Inter_shared_storage = findViewById(R.id.Inter_shared_storage)

        // textView2.text = MyApplication.Testname
        mstoragemanager = getSystemService(STORAGE_SERVICE) as StorageManager
        TerDao = AppDataBase.getDatabase(this@TestReportActivity).getTeacherDao()

        mAm = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val systemMemInfo = ActivityManager.MemoryInfo()
        mAm!!.getMemoryInfo(systemMemInfo)
        val mTotalMemory = systemMemInfo.totalMem / 1024 / 1024 / 1000.00
        Serial_number4 = findViewById(R.id.Serial_number4)

        finish_buton.setOnClickListener {
            finish()
        }

        baserecycleradapter = object : BaseRecyclerAdapter<Teacher>(
            this@TestReportActivity,
            null,
            R.layout.terdao_imet
        ) {
            override fun convert(
                holder: BaseRecyclerHolder?,
                data: Teacher?,
                position: Int,
                isScrolling: Boolean,
                selectedPosition: Int,
            ) {
                val image_name = holder?.getView<ImageView>(R.id.image_name)
                val name_text = holder?.getView<TextView>(R.id.name_text)
                val masg_text = holder?.getView<TextView>(R.id.masg_text)
                name_text?.text = data?.teacherName
                if (data?.TeacherType == 1) {
                    image_name?.setImageResource(R.drawable.ic_extension)
                    masg_text?.setTextColor(Color.parseColor("#FF3A3A"))
                    masg_text?.text = application.getText(R.string.Failed)
                } else {
                    image_name?.setImageResource(R.drawable.ic_checkcircle__)
                    masg_text?.setTextColor(Color.parseColor("#56F21F"))
                    masg_text?.text = application.getText(R.string.Passed)
                }
            }
        }
        var AllStudents =
            StudentDataBase(this@TestReportActivity).studentDao.getStudneBStudentType(true)
        var StudentID = AllStudents.map { it.StudentID?.toLong() }.toMutableList()
         StudentID.add(0L)
        var getAllTeachers = TeacherDataBase.getInstance(this).teacherDao.getAllTeachers()
        //  StudentID.plus(0)
        recyclerView.adapter = baserecycleradapter
        baserecycleradapter.setNewData(
            TeacherDataBase.getInstance(this).teacherDao.getTeacherIDList(
                StudentID
            )
        )
        setLinst()

        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalCounts = statFs.blockCountLong //总共的block数
        val size = statFs.blockSizeLong //每格所占的大小，一般是4KB==
        val totalROMSize = totalCounts * size //内部存储总大小

        val euntotalBytes = totalROMSize?.div(1024)?.div(1024)?.div(1024.00)?.let { BigDecimal(it) }
        val suntotalBytes = euntotalBytes?.setScale(2, BigDecimal.ROUND_HALF_DOWN)

        Serial_number6.text = DateUtil.formatDefaultDate(DateUtil.currentDate)
        Serial_number7.text =
            DateUtil.formatDateByFormat(DateUtil.currentDate, DateUtil.TIMES_HOUR_MIN_PATTERN)

        Serial_number.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Build.getSerial()
        } else {
            Build.SERIAL
        }

        Serial_number1.text = Build.MODEL
        Serial_number2.text = Build.VERSION.RELEASE
        Serial_number3.text = Build.DISPLAY
        Serial_number5.text = "${suntotalBytes} G"
        Inter_shared_storage.text = mTotalMemory.toString() + "G"

        try {
            NetworkUtil.getMacAddressTextView(Serial_number4)
        } catch (e: Exception) {
        }

    }


    @SuppressLint("NewApi")
    fun setLinst() {
        GlobalScope.launch {
            var stringbuilder: StringBuilder = StringBuilder()

            var AllStudents =
                StudentDataBase(this@TestReportActivity).studentDao.getStudneBStudentType(true)
            var StudentID = AllStudents.map { it.StudentID?.toLong() }.toMutableList()
            StudentID.add(0L)

            TerDao.getTeacherIDList(StudentID).forEach { TerDaoIt ->
                val TeacherType = when (TerDaoIt.TeacherType) {
                    1 -> "_失败"
                    2 -> "_通过"
                    else -> ""
                }
                stringbuilder?.append(TerDaoIt.stopDate + "_" + TerDaoIt.teacherName + TeacherType)
                stringbuilder?.append("\n")
            }
            writeSDFile(stringbuilder.toString())
            GlobalScope.launch {
                TerDao.deleteAll()
            }


        }

    }


}