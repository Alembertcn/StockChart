package com.androidx.stockchart.util

import android.text.TextUtils
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.TimeZone
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Administrator on 2016/6/18.
 */
object DataTimeUtil {
    var overTime: Int = 6000

    /**
     * 亿元以上格式
     */
    fun getPointTwo(number: Double): String {
        if (number == 0.0) {
            return "0"
        }
        val shuzi = number.toString() + ""
        if (shuzi.contains("E")) {
            val df = DecimalFormat("#.##")
            return df.format(shuzi.substring(0, shuzi.length - 2).toDouble()) + shuzi.substring(
                shuzi.length - 2,
                shuzi.length
            )
        }
        return "0"
    }

    /**
     * 普通double保留两位小数
     */
    fun getPointTwoNo(number: Double?): String {
        val df = DecimalFormat("######0.00")
        return df.format(number)
    }

    fun choiceUserLine(local: Int, pan: Int): String {
        if (pan == 0 && local == 0) {
            return "WMCache"
        } else if (pan == 1 && local == 0) {
            return "WSCache"
        } else if (pan == 0 && local == 1) {
            return "NMCache"
        } else if (pan == 1 && local == 1) {
            return "NSCache"
        }
        return "NMCache"
    }

    //浮点型判断
    fun isDecimal(str: String?): Boolean {
        if (str == null || "" == str) {
            return false
        }
        val pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*")
        return pattern.matcher(str).matches()
    }

    //是否为整形
    fun isNumeric(str: String): Boolean {
        var i = str.length
        while (--i >= 0) {
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    }

    // a integer to xx:xx:xx
    fun secToTime(time: Int): String {
        var timeStr: String? = null
        var hour = 0
        var minute = 0
        var second = 0
        if (time <= 0) return "00:00:00"
        else {
            minute = time / 60
            if (minute < 60) {
                second = time % 60
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second)
            } else {
                hour = minute / 60
                if (hour > 99) return "99:59:59"
                minute = minute % 60
                second = time - hour * 3600 - minute * 60
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second)
            }
        }
        return timeStr
    }

    var format: SimpleDateFormat = SimpleDateFormat("HH:mm")

    // a integer to xx:xx
    fun secToTime(time: Long): String? {
        var timeStr: String? = null
        timeStr = format.format(Date(time))
        return timeStr
    }

    // a integer to xx:xx
    val tem: SimpleDateFormat = SimpleDateFormat()

    @JvmStatic
    val temCalendar1=Calendar.getInstance(TimeZone.getDefault())
    @JvmStatic
    val temCalendar2=Calendar.getInstance(TimeZone.getDefault())
    private val temDate =Date()
    @JvmStatic
    fun secToTime(time: Long, format: String?): String? {
        temDate.time = time
        var timeStr: String? = null
        tem.applyLocalizedPattern(format)
        timeStr = tem.format(temDate)
        return timeStr
    }
    @JvmStatic
    fun secToTime(time: Long, format:SimpleDateFormat): String? {
        temDate.time = time
        return format.format(temDate)
    }

    // a integer to xxxxxx
    fun secToDate(time: Long): String? {
        var timeStr: String? = null
        //        timeStr = new SimpleDateFormat("yyyy/MM/dd").format(new Date(time));
        tem.applyLocalizedPattern("yyyy/MM/dd")
        timeStr = tem.format(Date(time))
        return timeStr
    }

    // a integer to xxxxxx
    fun secToDateMonth(time: Long): String? {
        var timeStr: String? = null
        tem.applyLocalizedPattern("yyyy/MM")
        timeStr = tem.format(Date(time))
        return timeStr
    }

    // a integer to xxxxxx
    @JvmStatic
    fun secToDateForFiveDay(time: Long): String? {
        var timeStr: String? = null
        tem.applyLocalizedPattern("MM-dd")
        timeStr = tem.format(Date(time))

        return timeStr
    }

    val startDate: String
        get() {
            tem.applyLocalizedPattern("yyyyMMdd")

            return tem.format(Calendar.getInstance().time)
        }

    val today: String
        get() {
            tem.applyLocalizedPattern("yyyy-MM-dd")
            return tem.format(Calendar.getInstance().time)
        }

    val endPlusOneDate: String
        get() {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR] + 1 //年份加一
            val month = calendar[Calendar.MONTH] + 1 //月份为当月
            val day = calendar[Calendar.DAY_OF_MONTH]
            return year.toString() + unitFormat(month).toString() + unitFormat(day).toString()
        }

    val endTime: String
        get() {
            tem.applyLocalizedPattern("HH:mm:ss")
            return tem.format(Calendar.getInstance().time)
        }

    val todayDate: String
        get() {
            tem.applyLocalizedPattern("yyyy-MM-dd")
            return tem.format(Calendar.getInstance().time)
        }

    fun unitFormat(i: Int): String {
        var retStr: String? = null
        retStr = if (i >= 0 && i < 10) "0$i"
        else "" + i
        return retStr
    }

    fun formatPhone(phone: String): String {
        return phone.substring(0, 3) + "****" + phone.substring(7, phone.length)
    }

    fun formatAccount(account: String): String {
        return account[0].toString() + "***" + account[account.length - 1]
    }

    fun formatName(name: String): String {
        if (TextUtils.isEmpty(name)) {
            return "*"
        } else {
            var temp = ""
            for (i in 0 until name.length - 1) {
                temp += "*"
            }
            return temp + name[name.length - 1]
        }
    }

    fun formatMail(mail: String): String {
        val mails = mail.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (TextUtils.isEmpty(mails[0])) {
            "*" + mails[1]
        } else {
            if (mails[0].length == 1 || mails[0].length == 2) {
                mails[0] + "@" + mails[1]
            } else {
                mails[0][0].toString() + "****" + mails[0][mails[0].length - 1] + "@" + mails[1]
            }
        }
    }

    fun formatCardID(cardID: String): String {
        if (TextUtils.isEmpty(cardID)) {
            return "****"
        } else if (cardID.length <= 6) {
            return cardID
        } else {
            val length = cardID.length - 6
            if (length <= 4) {
                return cardID.substring(0, 2) + "****" + cardID.substring(
                    cardID.length - 4,
                    cardID.length
                )
            } else {
                var hide = ""
                for (i in 0 until length) {
                    hide += "*"
                }
                return cardID.substring(0, 2) + hide + cardID.substring(
                    cardID.length - 4,
                    cardID.length
                )
            }
        }
    }

    fun formatBankCard(bankCard: String): String {
        if (TextUtils.isEmpty(bankCard)) {
            return "****"
        } else if (bankCard.length <= 4) {
            return bankCard
        } else {
            var temp = ""
            for (i in 0 until bankCard.length - 4) {
                temp += "*"
            }
            temp += bankCard.substring(bankCard.length - 4, bankCard.length)
            return temp
        }
    }

    //    public static boolean isEmail(String email){
    //        //正则表达式
    //        String regex = "^[A-Za-z]{1,40}@[A-Za-z0-9]{1,40}\\.[A-Za-z]{2,3}$";
    //        return email.matches(regex);
    //    }
    fun isEmail(string: String?): Boolean {
        if (string == null) return false
        val regEx1 =
            "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
        val m: Matcher
        val p = Pattern.compile(regEx1)
        m = p.matcher(string)
        return if (m.matches()) true
        else false
    }

    fun getMonthDay(before: Int): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, -before)
        val date = calendar.time
        val sdf = SimpleDateFormat("MM/dd")
        return sdf.format(date)
    }

    fun formatDate(xValues: MutableList<String>, isAddFirst: Boolean): List<String> {
        if (isAddFirst || xValues.size == 1) {
            val today = xValues[0]
            val sdf = SimpleDateFormat("yyyymmDD")
            val calendar = Calendar.getInstance() //获取日历实例
            try {
                calendar.time = sdf.parse(today)
                calendar.add(Calendar.DAY_OF_MONTH, -1) //设置为前一天
                val yesterday = sdf.format(calendar.time) //获得前一天
                xValues.add(0, yesterday)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        for (i in xValues.indices) {
            val temp = xValues[i]
            if (temp.length == 8) {
                xValues[i] = temp.substring(4, 6) + "/" + temp.substring(6, 8)
            }
        }
        return xValues
    }

    //获得当天的日期
    fun lastDay(): String {
        tem.applyLocalizedPattern("yyyy-MM-dd")
        val dateString = tem.format(Date())
        return dateString
    }

    fun yesterday(): String {
        var date = Date() //取时间
        val calendar: Calendar = GregorianCalendar()
        calendar.time = date
        calendar.add(Calendar.DATE, -1) //把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.time //这个时间就是日期往后推一天的结果
        tem.applyLocalizedPattern("yyyy-MM-dd")
        return tem.format(date)
    }

    //之前的几周
    fun getWeekBefore(week: Int): String {
//        String paramStartDate = "";
//        String paramEndDate = "";
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val dateNow = Date()
        var dateBefore = Date()
        val cal = Calendar.getInstance()
        cal.time = dateNow
        cal.add(Calendar.DAY_OF_MONTH, -week * 7)
        dateBefore = cal.time
        //        paramEndDate = sdf.format(dateNow);
        return sdf.format(dateBefore)
    }

    //之前的几个月
    fun getMonthBefore(month: Int): String {
//        String paramStartDate = "";
//        String paramEndDate = "";
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val dateNow = Date()
        var dateBefore = Date()
        val cal = Calendar.getInstance()
        cal.time = dateNow
        cal.add(Calendar.MONTH, -1 * month)
        dateBefore = cal.time
        //        paramEndDate = sdf.format(dateNow);
        return sdf.format(dateBefore)
    }

    fun getYesterday(today: String?): String {
        var yesterday = ""
        tem.applyLocalizedPattern("yyyymmDD")

        val calendar = Calendar.getInstance() //获取日历实例
        try {
            calendar.time = tem.parse(today)
            calendar.add(Calendar.DAY_OF_MONTH, -1) //设置为前一天
            yesterday = tem.format(calendar.time) //获得前一天
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return yesterday
    }

    fun formatYValue(value: Float, axisMaximum: Float): Float {
        if (axisMaximum >= 100 && axisMaximum < 10000) {
            return ((value / 10).toInt() * 10).toFloat()
        } else if (axisMaximum >= 10000) {
            return ((value / 100).toInt() * 100).toFloat()
        }
        return value
    }

    fun resetMaxValue(axisMaximum: Float, axisMinimum: Float): IntArray {
        var space = axisMaximum - axisMinimum
        val maxMin = IntArray(2)
        maxMin[0] = axisMaximum.toInt()
        maxMin[1] = axisMinimum.toInt()
        if (space <= 6000 && space > 60) { //10的倍数最大间隔200,最小间隔20
            if (space % 60 > 0) {
                space += 60f
            }
            val min = ((axisMinimum / 10).toInt()) * 10
            maxMin[0] = space.toInt() + min
            maxMin[1] = min
        } else if (space < 60000 && space > 6000) { //最小间隔300
            if (space % 600 > 0) {
                space += 600f
            }
            val min = ((axisMinimum / 100).toInt()) * 100
            maxMin[0] = space.toInt() + min
            maxMin[1] = min
        } else if (space > 60000) {
            if (space % 6000 > 0) {
                space += 6000f
            }
            val min = ((axisMinimum / 1000).toInt()) * 1000
            maxMin[0] = space.toInt() + min
            maxMin[1] = min
        }
        return maxMin
    }


    @JvmStatic
    fun isSameMoth(time1: Long, time2: Long): Boolean {
        temCalendar1.timeInMillis = time1
        temCalendar2.timeInMillis = time2
        return temCalendar1.get(Calendar.MONTH) ==temCalendar2.get(Calendar.MONTH) && temCalendar1.get(Calendar.YEAR) ==temCalendar2.get(Calendar.YEAR)
    }

    @JvmStatic
    fun isSameYear(time1: Long, time2: Long): Boolean {
        temCalendar1.timeInMillis = time1
        temCalendar2.timeInMillis = time2
        return temCalendar1.get(Calendar.YEAR) ==temCalendar2.get(Calendar.YEAR)
    }

    @JvmStatic
    fun isSameDay(time1: Long, time2: Long): Boolean {
        temCalendar1.timeInMillis = time1
        temCalendar2.timeInMillis = time2
        return temCalendar1.get(Calendar.MONTH) ==temCalendar2.get(Calendar.MONTH) && temCalendar1.get(Calendar.DAY_OF_MONTH) ==temCalendar2.get(Calendar.DAY_OF_MONTH)
    }

    @JvmStatic
    fun isSameMini(time1: Long, time2: Long): Boolean {
        return secToTime(time1, "m") == secToTime(time2, "m")
    }

    private const val MILLIS_PER_HALF_HOUR = 30 * 60 * 1000L // 1800000L
    @JvmStatic
    fun isSameHalfHour(time1: Long, time2: Long): Boolean {
        val s1 = secToTime(time1, "mm")?.substring(0)?.toInt()?:0
        val s2 = secToTime(time2, "mm")?.substring(0)?.toInt()?:0
        return (s1<3 && s2 <3) || (s1>=3 && s2>=3)
    }

    /**
     * 是否是整半点
     * @param dateMills
     * @return
     */
    @JvmStatic
    fun isHalfHourTimePoint(dateMills: Long): Boolean {
        return dateMills % MILLIS_PER_HALF_HOUR == 0L
    }
}
