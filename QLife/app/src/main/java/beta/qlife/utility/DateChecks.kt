package beta.qlife.utility

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Alex Ruffo on 2020-06-01
 * A collection of useful functions for checking against special dates
 */

internal class DateChecks {
    fun dateIsCloseToNewTerm(): Boolean {
        val now = Calendar.getInstance()

        val startOfFallTerm = Calendar.getInstance();
        startOfFallTerm.set(Calendar.MONTH, 7)      // months start at *0*

        val diffBetweenNowAndStartOfFallTerm = getNumberOfDaysBetweenTwoCalendars(now, startOfFallTerm)
        if (diffBetweenNowAndStartOfFallTerm > -10 && diffBetweenNowAndStartOfFallTerm <= 0)     // if now is after 10 days before the start of the fall term,
             return true                                                                         // but not after the start of the fall term (start - 10 days < now < start day)

        val startOfWinterTerm = Calendar.getInstance();
        startOfWinterTerm.set(Calendar.MONTH, 0)    // start of Jan

        val diffBetweenNowAndStartOfWinterTerm = getNumberOfDaysBetweenTwoCalendars(now, startOfWinterTerm)

        if (diffBetweenNowAndStartOfWinterTerm > -10 && diffBetweenNowAndStartOfWinterTerm <= 0) // if now is after 10 days before the start of the winter term,
            return true                                                                          // but not after the start of the winter term (start - 10 days < now < start day)

        val startOfSummerTerm = Calendar.getInstance();
        startOfSummerTerm.set(Calendar.MONTH, 5)    // start of may

        val diffBetweenNowAndStartOfSummerTerm = getNumberOfDaysBetweenTwoCalendars(now, startOfSummerTerm)

        if (diffBetweenNowAndStartOfSummerTerm > -10 && diffBetweenNowAndStartOfSummerTerm <= 0) // if now is after 10 days before the start of the summer term,
            return true                                                                          // but not after the start of the summer term (start - 10 days < now < start day)

        return false
    }

    private fun getNumberOfDaysBetweenTwoCalendars(day1: Calendar, day2: Calendar): Long {
        day1.set(Calendar.HOUR, 0)
        day1.set(Calendar.MINUTE, 0)
        day1.set(Calendar.SECOND, 0)
        day1.set(Calendar.MILLISECOND, 0)

        day2.set(Calendar.HOUR, 0)
        day2.set(Calendar.MINUTE, 0)
        day2.set(Calendar.SECOND, 0)
        day2.set(Calendar.MILLISECOND, 0)

        return TimeUnit.MILLISECONDS.toDays(day1.timeInMillis - day2.timeInMillis)  // gives a - value if day1 is before day 2, and + if day1 is after day2
    }
}