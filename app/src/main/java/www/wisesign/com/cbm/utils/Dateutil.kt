package www.wisesign.com.cbm.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

object Dateutil {
    var YMD = "yyyy-MM-dd"
    var YMDHMS = "yyyy-MM-dd HH:mm:ss"

    fun currentDate(type: String): String {
        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat(type)
        return dateFormat.format(date)
    }
}
