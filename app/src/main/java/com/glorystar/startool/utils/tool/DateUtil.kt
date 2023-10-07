package com.glorystar.startool.utils.tool

import android.annotation.SuppressLint
import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 */
@SuppressLint("SimpleDateFormat")
object DateUtil {
    var TIMES_HOUR_MIN_PATTERN = "HH:mm"
    var TIMES_PATTERN = "HH:mm:ss"
    var TIMESTAMP_DAY_HOUR_PATTERNR = "MM-dd HH:mm"
    var TIMESTAMP_DAY_HOUR_PATTERNR_2 = "MM/dd HH:mm"
    var MonthTimeFormat = "MM月dd日 HH:mm"
    var NOCHAR_PATTERNYMD = "yyyyMMdd"
    var DEFAULT_PATTERN = "yyyy-MM-dd"
    var DIR_PATTERN = "yyyy/MM/dd"
    var TIMESTAMP_PATTERNR = "yyyy-MM-dd\nHH:mm"
    var NOCHAR_PATTERN = "yyyyMMddHHmmss"
    var TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss"
    var TIMESTAMP_PATTERN_2 = "yyyy/MM/dd HH:mm:ss"

    /**
     * @param //date
     */
    fun getWeek(timeStamp: Long): String {
        var mydate = 0
        var week = ""
        try {
            val cd = Calendar.getInstance()
            cd.time = Date(timeStamp)
            mydate = cd[Calendar.DAY_OF_WEEK]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when (mydate) {
            1 -> {
                week = "周日"
            }
            2 -> {
                week = "周一"
            }
            3 -> {
                week = "周二"
            }
            4 -> {
                week = "周三"
            }
            5 -> {
                week = "周四"
            }
            6 -> {
                week = "周五"
            }
            7 -> {
                week = "周六"
            }
        }
        return week
    }

    fun formatDateByFormat(date: Date?, format: String?): String {
        var result = ""
        if (date != null && !TextUtils.isEmpty(format)) {
            try {
                val sdf = SimpleDateFormat(format)
                result = sdf.format(date)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return result
    }

    fun formatDefaultDate(date: Date?): String {
        return formatDateByFormat(date, DEFAULT_PATTERN)
    }


    fun formatDirDate(date: Date?): String {
        return formatDateByFormat(date, DIR_PATTERN)
    }


    fun formatTimesTampDate(date: Date?): String {
        return formatDateByFormat(date, TIMESTAMP_PATTERN)
    }


    fun formatTimesDate(date: Date?): String {
        return formatDateByFormat(date, TIMES_PATTERN)
    }

    val timesDate: String
        get() = formatDateByFormat(Date(), TIMES_PATTERN)


    fun formatNoCharDate(date: Date?): String {
        return formatDateByFormat(date, NOCHAR_PATTERN)
    }




    fun parseDate(strDate: String?, pattern: String?): Date? {
        try {
            val format = SimpleDateFormat(pattern)
            return format.parse(strDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun parseDefaultDate(date: String?): Date? {
        return parseDate(date, DEFAULT_PATTERN)
    }


    fun parseDefaultDate_2(date: String?): Date? {
        return parseDate(date, "yyyy-MM-dd HH:mm")
    }


    fun parseTimesTampDate(date: String?): Date? {
        return parseDate(date, TIMESTAMP_PATTERN)
    }


    val timeStamp: String
        get() {
            val t1 = timeStamp_L
            return t1.toString()
        }


    val timeStamp_L: Long
        get() = System.currentTimeMillis()


    val currentDate: Date
        get() {
            val calendar = Calendar.getInstance()
            return calendar.time
        }


    fun getYear(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        return c[Calendar.YEAR]
    }

    fun getMonth(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        return c[Calendar.MONTH] + 1
    }

    fun getWeek(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        var dayOfWeek = c[Calendar.DAY_OF_WEEK]
        dayOfWeek = dayOfWeek - 1
        if (dayOfWeek == 0) {
            dayOfWeek = 7
        }
        return dayOfWeek
    }

    fun getDay(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        return c[Calendar.DAY_OF_MONTH]
    }

    fun getHour(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        return c[Calendar.HOUR_OF_DAY]
    }

    fun getMinute(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        return c[Calendar.MINUTE]
    }

    fun getSecond(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        return c[Calendar.SECOND]
    }

    fun getMillis(date: Date?): Long {
        val c = Calendar.getInstance()
        c.time = date
        return c.timeInMillis
    }

    fun addDate(date: Date?, day: Int): Date {
        val c = Calendar.getInstance()
        c.timeInMillis = getMillis(date) + day.toLong() * 24 * 3600 * 1000
        return c.time
    }

    fun diffDate(date: Date?, date1: Date?): Int {
        return ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000)).toInt()
    }

    fun diffDateTime(date: Date?, date1: Date?): Long {
        return ((getMillis(date) - getMillis(date1)) / 1000)
    }

    fun getYearMD(date: String?): String {
        var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val d: Date?
        d = try {
            sdf.parse(date)
        } catch (e: ParseException) {
            Date()
        }
        sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(d)
    }

    fun formatTimesDate_hour(timeStamp: String?): String {
        val sdf = SimpleDateFormat(TIMESTAMP_DAY_HOUR_PATTERNR)
        val lcc = java.lang.Long.valueOf(timeStamp)
        // int i = Integer.parseInt(time);
        return sdf.format(Date(lcc * 1000L))
    }

    fun formatTimesHour_minute(timeStamp: String?): String {
        val sdf = SimpleDateFormat(TIMES_HOUR_MIN_PATTERN)
        val lcc = java.lang.Long.valueOf(timeStamp)
        // int i = Integer.parseInt(time);
        return sdf.format(Date(lcc * 1000L))
    }

    fun formatTimesYMD_Hour_minute(timeStamp: String?): String {
        val sdf = SimpleDateFormat(TIMESTAMP_PATTERNR)
        val lcc = java.lang.Long.valueOf(timeStamp)
        // int i = Integer.parseInt(time);
        return sdf.format(Date(lcc * 1000L))
    }

    fun getTimeString(timestamp: Long): String {
        var result = ""
        val weekNames = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val hourTimeFormat = "HH:mm"
        val monthTimeFormat = "MM月dd日 HH:mm"
        val yearTimeFormat = "yyyy年MM月dd日 HH:mm"
        return try {
            // 获得当前时间
            val todayCalendar = Calendar.getInstance()
            // 获得聊天时间
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            // 时间判断。
            result = if (todayCalendar[Calendar.YEAR] == calendar[Calendar.YEAR]) { // 当年
                if (todayCalendar[Calendar.MONTH] == calendar[Calendar.MONTH]) { // 当月
                    // 时间差。
                    val temp =
                        todayCalendar[Calendar.DAY_OF_MONTH] - calendar[Calendar.DAY_OF_MONTH]
                    when (temp) {
                        0 -> getTime(timestamp, hourTimeFormat)
                        1 -> "昨天 " + getTime(timestamp, hourTimeFormat)
                        2, 3, 4, 5, 6 -> {
                            val dayOfWeek = calendar[Calendar.DAY_OF_WEEK] // 星期几。
                            weekNames[dayOfWeek - 1] + " " + getTime(timestamp, hourTimeFormat)
                        }
                        else -> getTime(timestamp, monthTimeFormat) // 当月时间。
                    }
                } else {
                    getTime(timestamp, monthTimeFormat) // 隔月时间。
                }
            } else {
                getTime(timestamp, yearTimeFormat) // 隔年时间。
            }
            result
        } catch (e: Exception) {
            ""
        }
    }

    fun getTimeString_MD(timestamp: Long): String {
        var result = ""
        val weekNames = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val hourTimeFormat = "HH:mm"
        val monthTimeFormat = "MM月dd日"
        val yearTimeFormat = "yyyy年MM月dd日"
        return try {
            // 获得当前时间
            val todayCalendar = Calendar.getInstance()
            // 获得聊天时间
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            // 时间判断。
            result = if (todayCalendar[Calendar.YEAR] == calendar[Calendar.YEAR]) { // 当年
                if (todayCalendar[Calendar.MONTH] == calendar[Calendar.MONTH]) { // 当月
                    // 时间差。
                    val temp =
                        todayCalendar[Calendar.DAY_OF_MONTH] - calendar[Calendar.DAY_OF_MONTH]
                    when (temp) {
                        0 -> {
                            val minute =
                                getTimeMinuteDiff(timestamp, todayCalendar.timeInMillis) // 时间差。
                            if (minute <= 5) {
                                "刚刚"
                            } else {
                                getTime(timestamp, hourTimeFormat)
                            }
                        }
                        1 -> "昨天"
                        2, 3, 4, 5, 6 -> {
                            val dayOfWeek = calendar[Calendar.DAY_OF_WEEK] // 星期几。
                            weekNames[dayOfWeek - 1]
                        }
                        else -> getTime(timestamp, monthTimeFormat) // 当月时间。
                    }
                } else {
                    getTime(timestamp, monthTimeFormat) // 隔月时间。
                }
            } else {
                getTime(timestamp, yearTimeFormat) // 隔年时间。
            }
            result
        } catch (e: Exception) {
            ""
        }
    }

    fun getTime(time: Long, pattern: String?): String {
        val date = Date(time)
        return dateFormat(date, pattern)
    }

    fun dateFormat(date: Date?, pattern: String?): String {
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    fun getTimeMinuteDiff(timestamp: Long, timestampTwo: Long): Long {
        return try {
            // 时间差。
            val minute = (timestampTwo - timestamp) / 1000 / 60
            Math.abs(minute)
        } catch (e: Exception) {
            0
        }
    }

    fun getTimeMillisDiff(timestamp: Long, timestampTwo: Long): Long {
        return try {
            // 时间差。
            val millis = (timestampTwo - timestamp) / 1000
            Math.abs(millis)
        } catch (e: Exception) {
            0
        }
    }

    fun formatTimesHour_minute_second(timeStamp: Long): String {
        val sdf = SimpleDateFormat(TIMES_PATTERN)
        //long lcc = Long.valueOf(timeStamp);
        // int i = Integer.parseInt(time);
        return sdf.format(Date(timeStamp * 1000L))
    }

    private val msFormat = SimpleDateFormat("mm:ss")

    fun timeParse(duration: Long): String {
        var time = ""
        try {
            if (duration > 1000L) {
                time = timeParseMinute(duration)
            } else {
                val minute = duration / 60000L
                val seconds = duration % 60000L
                val second = Math.round(seconds.toFloat() / 1000.0f).toLong()
                if (minute < 10L) {
                    time = time + "0"
                }
                time = "$time$minute:"
                if (second < 10L) {
                    time = time + "0"
                }
                time = time + second
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return time
    }

    fun timeParseMinute(duration: Long): String {
        return try {
            msFormat.format(java.lang.Long.valueOf(duration))
        } catch (var3: Exception) {
            var3.printStackTrace()
            "0:00"
        }
    }

    @Throws(ParseException::class)
    fun timeSubtraction(time1: Long, time2: Long): String {
        /**
         * @description time2 是大的时间
         * @param  [time1, time2]
         * @return java.lang.String
         */
        val result = time2 - time1 //获取两时间相差的毫秒数
        val nd = (1000 * 24 * 60 * 60).toLong()
        val nh = (1000 * 60 * 60).toLong()
        val nm = (1000 * 60).toLong()
        val ns: Long = 1000
        val hour = result % nd / nh //获取相差的小时数
        val min = result % nd % nh / nm //获取相差的分钟数
        val sec = result % nd % nh % nm / ns //获取相差的秒钟数
        val day = result / nd
        val formatter = SimpleDateFormat("mm:ss") //初始化Formatter的转换格式。
        val hMiles = hour * 3600000 //小时数转换成毫秒
        val mMiles = min * 60000 //分钟数转换成毫秒
        val sMiles = sec * 1000 //秒钟数转换成毫秒
        val resulMiles = hMiles + mMiles + sMiles

        //下面这段很重要 ,计算之后设置时区,不然会差几小时
        formatter.timeZone = TimeZone.getTimeZone("GMT+00:00")
        //我这段是在一天内计算的 如果大于一天 就把下面的 day*24加到小时上就可以了
        return formatter.format(sec * 1000)
    }

    @Throws(ParseException::class)
    fun timeSubtraction_S(time1: Long, time2: Long): String {
        val result = time2 - time1 // 获取两时间相差的毫秒数
        val nd = (1000 * 24 * 60 * 60).toLong()
        val nh = (1000 * 60 * 60).toLong()
        val nm = (1000 * 60).toLong()
        val ns: Long = 1000
        val day = result / nd // 获取相差的天数
        var hour = result % nd / nh //获取相差的小时数
        val min = result % nd % nh / nm //获取相差的分钟数
        val sec = result % nd % nh % nm / ns //获取相差的秒钟数
        val sBuffer = StringBuilder()
        if (day > 0) {
            hour = hour + day * 24
        }
        if (hour > 0) {
            sBuffer.append(hour).append("h ")
        }
        if (min > 0) {
            sBuffer.append(min).append("m")
        } else {
            if (sec > 0) {
                sBuffer.append(sec).append("s")
            }
        }
        return sBuffer.toString()
    }

    fun date2TimeStamp(date_str: String?, format: String?): String {
        try {
            val sdf = SimpleDateFormat(format)
            return sdf.parse(date_str).time.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun date2TimeStamp_L(date_str: String?, format: String?): Long {
        try {
            val sdf = SimpleDateFormat(format)
            return sdf.parse(date_str).time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun stampToTime(stamp: String): String? {
        var sd = ""
        val d = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        sd = sdf.format(Date(stamp.toLong())) // 时间戳转换日期
        return sd
    }

 fun stampToTies(stamp: String): String? {
     var sd = ""
     val d = Date()
     val sdf = SimpleDateFormat("yyyyMMddHHmmss")
     sd = sdf.format(Date(stamp.toLong())) // 时间戳转换日期
     return sd
 }
}