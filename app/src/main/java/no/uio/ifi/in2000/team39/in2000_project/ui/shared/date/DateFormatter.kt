package no.uio.ifi.in2000.team39.in2000_project.ui.shared.date

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Provides utility functions to format date strings according to specified patterns. It handles parsing and formatting
 * for full dates with time and just the hour component.
 */
object DateFormatter {
    private const val DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
    private const val DISPLAY_FULL_DAY_FORMAT_PATTERN = "MMM dd, yyyy HH:mm"
    private const val DISPLAY_HOUR_FORMAT_PATTERN = "HH:mm"

    /**
     * Formats a provided ISO8601 date string into a full date with time format 'MMM dd, yyyy HH:mm'.
     * If the input is null or the parsing fails, it returns null.
     *
     * Example:
     * val dateExample = DateFormatter.fullDayFormat("2023-05-10T14:00:00")
     * // Output: "May 10, 2023 14:00"
     */
    fun fullDayFormat(dateString: String?): String? {
        if (dateString == null) return null

        val currentLocale = Locale.getDefault()

        val parser = SimpleDateFormat(DATE_FORMAT_PATTERN, currentLocale)
        val formatter = SimpleDateFormat(DISPLAY_FULL_DAY_FORMAT_PATTERN, currentLocale)

        val date = parser.parse(dateString)
        return date?.let { formatter.format(it) }
    }

    /**
     * Converts an ISO8601 date string to a time format 'HH:mm', showing only the hours and minutes.
     * Returns null if the input is null or the parsing fails.
     *
     * Example:
     * val timeExample = DateFormatter.hourFormat("2023-05-10T14:00:00")
     * // Output: "14:00"
     */
    fun hourFormat(dateString: String?): String? {
        if (dateString == null) return null

        val currentLocale = Locale.getDefault()

        val parser = SimpleDateFormat(DATE_FORMAT_PATTERN, currentLocale)
        val formatter = SimpleDateFormat(DISPLAY_HOUR_FORMAT_PATTERN, currentLocale)

        val date = parser.parse(dateString)
        return date?.let { formatter.format(it) }
    }
}
