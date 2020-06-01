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
        startOfFallTerm.set(Calendar.MONTH, 7)

        val diffBetweenNowAndStartOfFallTerm = now.compareTo(startOfFallTerm)
        if (diffBetweenNowAndStartOfFallTerm > -(TimeUnit.DAYS.toMillis(10)) && diffBetweenNowAndStartOfFallTerm <= 0)     // if now is after 10 days before the start of the fall term,
             return true                                                                                                            // but not after the start of the fall term (start - 10 days < now < start day)

        val startOfWinterTerm = Calendar.getInstance();
        startOfWinterTerm.set(Calendar.MONTH, 1)

        val diffBetweenNowAndStartOfWinterTerm = now.compareTo(startOfWinterTerm)

        if (diffBetweenNowAndStartOfWinterTerm > -(TimeUnit.DAYS.toMillis(10)) && diffBetweenNowAndStartOfWinterTerm <= 0) // if now is after 10 days before the start of the winter term,
            return true                                                                                                             // but not after the start of the winter term (start - 10 days < now < start day)

        val startOfSummerTerm = Calendar.getInstance();
        startOfSummerTerm.set(Calendar.MONTH, 5)

        val diffBetweenNowAndStartOfSummerTerm = now.compareTo(startOfSummerTerm)

        if (diffBetweenNowAndStartOfSummerTerm > -(TimeUnit.DAYS.toMillis(10)) && diffBetweenNowAndStartOfSummerTerm <= 0) // if now is after 10 days before the start of the summer term,
            return true                                                                                                             // but not after the start of the summer term (start - 10 days < now < start day)
    }
}